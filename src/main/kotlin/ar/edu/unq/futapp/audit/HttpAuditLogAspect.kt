package ar.edu.unq.futapp.audit

import jakarta.servlet.http.HttpServletResponse
import org.apache.logging.log4j.LogManager
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation

@Aspect
@Component
class HttpAuditLogAspect {
    private val logger = LogManager.getLogger(HttpAuditLogAspect::class.java)

    private data class PropertyMeta(
        val name: String,
        val getter: (Any) -> Any?,
        val sensitive: Boolean
    )
    private val metaCache = ConcurrentHashMap<KClass<*>, List<PropertyMeta>>()

    @Around("within(ar.edu.unq.futapp.controller..*) && @within(org.springframework.web.bind.annotation.RestController)")
    fun logRequest(pjp: ProceedingJoinPoint): Any? {
        val startNs = System.nanoTime()
        val ts = Instant.now().toString()

        val (method, path) = currentHttpMethodAndPath()
        val params = redactParams(pjp)
        val user = currentUserOrAnonymous()

        return try {
            val result = pjp.proceed()
            val durationMs = (System.nanoTime() - startNs) / 1_000_000
            val status = extractStatus(result, currentResponse())
            logger.info("audit ts={}, user={}, method={}, path={}, status={}, params={}, timeMs={}", ts, user, method, path, status, params, durationMs)
            result
        } catch (ex: Throwable) {
            val durationMs = (System.nanoTime() - startNs) / 1_000_000
            val status = currentResponse()?.status ?: 500
            logger.warn("audit ts={}, user={}, method={}, path={}, status={}, params={}, timeMs={}, error={}", ts, user, method, path, status, params, durationMs, ex.toString())
            throw ex
        }
    }

    private fun redactParams(pjp: ProceedingJoinPoint): String {
        return try {
            val args = pjp.args
            val redacted = args.map { redactValue(it) }
            redacted.joinToString(prefix = "[", postfix = "]")
        } catch (_: Throwable) { "<params-unavailable>" }
    }

    private fun redactValue(value: Any?): String {
        if (value == null) return "null"
        if (value is CharSequence) {
            return redactStringIfSensitiveKey(null, value.toString())
        }
        if (value is Map<*, *>) {
            return try {
                val entries = value.entries.take(20).joinToString(prefix = "{", postfix = "}") { e ->
                    val key = e.key?.toString() ?: "null"
                    val v = e.value?.toString() ?: "null"
                    val rv = if (isSensitiveName(key)) redactStringKeepingLength(e.value) else v
                    "$key=$rv"
                }
                entries
            } catch (_: Throwable) { "<unprintable-map>" }
        }

        return try {
            val kClass = value::class
            val metas = metaCache.computeIfAbsent(kClass) { inspectClass(it) }
            if (metas.isEmpty()) {
                return runCatching { value.toString() }.getOrElse { "<unprintable>" }
            }
            val parts = metas.associate { m ->
                val v = runCatching { m.getter(value) }.getOrElse { null }
                val rendered = if (m.sensitive) redactStringKeepingLength(v) else runCatching { v?.toString() ?: "null" }.getOrElse { "<unprintable>" }
                m.name to rendered
            }
            parts.entries.joinToString(prefix = "{", postfix = "}") { (k, v) -> "$k=$v" }
        } catch (_: Throwable) {
            runCatching { value.toString() }.getOrElse { "<unprintable>" }
        }
    }

    private fun inspectClass(kClass: KClass<*>): List<PropertyMeta> {
        return try {
            kClass.declaredMemberProperties.mapNotNull { p ->
                @Suppress("UNCHECKED_CAST")
                val prop = p as? KProperty1<Any, Any?>
                val getterFn: (Any) -> Any? = { inst -> runCatching { prop?.get(inst) }.getOrNull() }
                val sensitive = p.findAnnotation<Sensitive>() != null || isSensitiveName(p.name)
                PropertyMeta(p.name, getterFn, sensitive)
            }
        } catch (_: Throwable) { emptyList() }
    }

    private fun redactStringKeepingLength(v: Any?): String {
        val s = v?.toString() ?: ""
        if (s.isEmpty()) return ""
        return "*".repeat(s.length.coerceAtMost(64))
    }

    private fun isSensitiveName(name: String): Boolean {
        val n = name.lowercase()
        return n in setOf("password", "pass", "authorization", "token", "refreshToken", "secret", "apiKey")
                || n.contains("password") || n.contains("token") || n.contains("secret")
    }

    private fun redactStringIfSensitiveKey(key: String?, value: String): String {
        val k = key?.lowercase()
        return if (k != null && isSensitiveName(k)) "***" else value
    }

    private fun currentHttpMethodAndPath(): Pair<String, String> {
        return try {
            val req = (RequestContextHolder.getRequestAttributes() as? ServletRequestAttributes)?.request
            if (req != null) {
                val method = req.method ?: "UNKNOWN"
                val uri = req.requestURI ?: req.requestURL?.toString() ?: "-"
                val qs = req.queryString
                val full = if (!qs.isNullOrBlank()) "$uri?$qs" else uri
                method to full
            } else "UNKNOWN" to "-"
        } catch (_: Throwable) { "UNKNOWN" to "-" }
    }

    private fun currentResponse(): HttpServletResponse? =
        (RequestContextHolder.getRequestAttributes() as? ServletRequestAttributes)?.response

    private fun extractStatus(result: Any?, res: HttpServletResponse?): Int {
        return when (result) {
            is ResponseEntity<*> -> result.statusCode.value()
            else -> res?.status ?: 200
        }
    }

    private fun currentUserOrAnonymous(): String {
        return try {
            val contextClass = Class.forName("org.springframework.security.core.context.SecurityContextHolder")
            val ctx = contextClass.getMethod("getContext").invoke(null)
            val auth = ctx.javaClass.getMethod("getAuthentication").invoke(ctx)
            if (auth != null) {
                val name = auth.javaClass.getMethod("getName").invoke(auth) as? String
                name ?: "anonymous"
            } else "anonymous"
        } catch (_: Throwable) { "anonymous" }
    }
}
