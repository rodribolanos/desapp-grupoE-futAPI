package ar.edu.unq.futapp.config

import ar.edu.unq.futapp.model.User
import ar.edu.unq.futapp.repository.UserRepository
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
        val users = listOf(
            User("testUser1", passwordEncoder.encode("Password123!")),
        )
        userRepository.saveAll(users)
        print("iniciando users")
    }
}