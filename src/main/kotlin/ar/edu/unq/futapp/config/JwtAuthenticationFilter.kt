package ar.edu.unq.futapp.config

import ar.edu.unq.futapp.service.UserService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtUtil: JwtUtil,
    private val userService: UserService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        val token = authHeader.substring(7)

        if (!jwtUtil.validateToken(token)) {
            filterChain.doFilter(request, response)
            return
        }

        val usernameOpt = jwtUtil.getUsername(token)
        if (usernameOpt.isEmpty) {
            filterChain.doFilter(request, response)
            return
        }

        val username = usernameOpt.get()

        if (SecurityContextHolder.getContext().authentication == null) {
            try {
                val userDetails = userService.loadUserByUsername(username)
                val authToken = UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.authorities
                )
                authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = authToken
            } catch (_: Exception) {
                // Silenciar errores de carga de usuario; continuar cadena
            }
        }

        filterChain.doFilter(request, response)
    }
}
