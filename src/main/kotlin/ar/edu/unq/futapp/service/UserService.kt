package ar.edu.unq.futapp.service

import ar.edu.unq.futapp.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByUsername(username)
        if (user.isEmpty) throw UsernameNotFoundException("Usuario no encontrado")
        user.get().let {
            return org.springframework.security.core.userdetails.User(
                it.username(),
                it.password(),
                emptyList()
            )
        }
    }

    fun register(username: String, password: String): Boolean {
        if (userRepository.findByUsername(username).isPresent) {
            return false
        }
        val user = ar.edu.unq.futapp.model.User(username, password)
        userRepository.save(user)
        return true
    }
}
