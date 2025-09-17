package ar.edu.unq.futapp.config

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.Claims
import io.jsonwebtoken.security.Keys
import java.util.Date
import org.springframework.stereotype.Component
import java.util.Optional

@Component
class JwtUtil {
    private val secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256)
    private val accessTokenExpiration = 10 * 60 * 1000 // 10 minutos en ms
    private val refreshTokenExpiration = 24 * 60 * 60 * 1000 // 1 día en ms

    fun generateToken(username: String, isRefresh: Boolean = false): String {
        val expiration = if (isRefresh) refreshTokenExpiration else accessTokenExpiration
        val builder = Jwts.builder()
            .setSubject(username)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + expiration))
        if (isRefresh) {
            builder.claim("type", "refresh")
        }
        return builder.signWith(secretKey).compact()
    }

    fun validateToken(token: String): Boolean {
        return try {
            val claims = getClaims(token)
            !claims.expiration.before(Date())
        } catch (e: Exception) {
            false
        }
    }

    fun getUsername(token: String): Optional<String> {
        return try {
            Optional.ofNullable(getClaims(token).subject)
        } catch (e: Exception) {
            Optional.empty()
        }
    }

    fun isRefreshToken(token: String): Boolean {
        return try {
            val claims = getClaims(token)
            claims["type"] == "refresh"
        } catch (e: Exception) {
            false
        }
    }

    private fun getClaims(token: String): Claims {
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).body
    }
}
