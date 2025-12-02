package ar.edu.unq.futapp.config

import ar.edu.unq.futapp.model.User
import ar.edu.unq.futapp.repository.UserRepository
import ar.edu.unq.futapp.utils.TestUserUtils
import jakarta.annotation.PostConstruct
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.security.crypto.password.PasswordEncoder

@TestConfiguration
class TestDataInitializer(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    @PostConstruct
    fun initializeTestData() {
        userRepository.deleteAll() // Limpia la base de datos antes de inicializar
        val username = TestUserUtils.existingUser().username
        val password = TestUserUtils.existingUser().password
        val users = listOf(
            User(username, passwordEncoder.encode(password)),
        )
        userRepository.saveAll(users)
        print("iniciando users")
    }
}