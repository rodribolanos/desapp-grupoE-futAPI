package ar.edu.unq.futapp.beans

import ar.edu.unq.futapp.exception.InvalidCredentialsException
import ar.edu.unq.futapp.model.User
import ar.edu.unq.futapp.repository.UserRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class AuthUtils(private val userRepository: UserRepository) {
    fun getCurrentUser(): User {
        val authentication = SecurityContextHolder.getContext().authentication
        val username = authentication?.name
        if (username.isNullOrBlank() || !authentication.isAuthenticated) {
            throw InvalidCredentialsException("No hay usuario autenticado")
        }
        return userRepository.findByUsername(username).orElseThrow {
            InvalidCredentialsException("Usuario no encontrado")
        }
    }
}