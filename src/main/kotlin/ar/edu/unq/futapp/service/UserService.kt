package ar.edu.unq.futapp.service

import ar.edu.unq.futapp.repository.UserRepository
import ar.edu.unq.futapp.model.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.security.crypto.password.PasswordEncoder
import ar.edu.unq.futapp.exception.UserAlreadyExistsException
import ar.edu.unq.futapp.exception.InvalidPasswordException

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val userOpt = userRepository.findByUsername(username)
        val user = userOpt.orElseThrow { UsernameNotFoundException("Usuario no encontrado") }
        return org.springframework.security.core.userdetails.User(
            user.username(),
            user.password(),
            emptyList()
        )
    }

    fun register(username: String, password: String) {
        if (userRepository.findByUsername(username).isPresent) {
            throw UserAlreadyExistsException("El usuario ya existe")
        }
        if (!isAcceptable(password)) {
            throw InvalidPasswordException("La contraseña debe tener al menos 8 caracteres, un número, un caracter especial, una mayúscula y una minúscula.")
        }
        val encodedPassword = passwordEncoder.encode(password)
        val user = User(username, encodedPassword)
        userRepository.save(user)
    }

    private fun isAcceptable(password: String): Boolean {
        val regex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#\\$%^&*()_+\\-={}|\\[\\]:;\"'<>,.?/]).{8,}")
        return regex.matches(password)
    }
}