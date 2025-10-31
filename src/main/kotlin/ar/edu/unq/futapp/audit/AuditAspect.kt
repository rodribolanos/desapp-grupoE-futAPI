package ar.edu.unq.futapp.audit

import ar.edu.unq.futapp.beans.AuthUtils
import ar.edu.unq.futapp.model.RequestAudit
import ar.edu.unq.futapp.repository.RequestAuditRepository
import jakarta.servlet.http.HttpServletResponse
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

@Aspect
@Component
class AuditAspect(
    private val repository: RequestAuditRepository,
    private val authUtils: AuthUtils
) {

    @Around("within(ar.edu.unq.futapp.controller..*) && @within(org.springframework.web.bind.annotation.RestController)")
    fun audit(pjp: ProceedingJoinPoint): Any? {
        val start = System.nanoTime()
        try {
            val result = pjp.proceed()
            val durationMs = (System.nanoTime() - start) / 1_000_000

            val attrs = RequestContextHolder.getRequestAttributes() as? ServletRequestAttributes
            val req = attrs?.request
            val res = attrs?.response

            val status: Int = extractStatus(result, res)

            if (status !in 200 .. 299) {
                return result
            }

            val method = req?.method?.uppercase() ?: "UNKNOWN"
            val uri = req?.requestURI ?: req?.requestURL?.toString() ?: "UNKNOWN"

            // Evitar auditar rutas públicas o que podrían autogenerar ruido
            if (uri.startsWith("/auth") || uri.startsWith("/api-docs") || uri.startsWith("/swagger") || uri.startsWith("/me/history")) {
                return result
            }

            val endpoint = "$method $uri"

            // Si no hay usuario autenticado, no auditar
            val userId = try {
                authUtils.getCurrentUser().username()
            } catch (_: Exception) {
                return result
            }

            repository.save(
                RequestAudit(
                    endpoint = endpoint,
                    status = status,
                    durationMs = durationMs,
                    userId = userId
                )
            )

            return result
        } catch (ex: Throwable) {
            throw ex
        }
    }

    private fun extractStatus(result: Any, res: HttpServletResponse?): Int {
        if (result is ResponseEntity<*>) {
            return result.statusCode.value()
        }
        return res?.status ?: 200
    }
}
