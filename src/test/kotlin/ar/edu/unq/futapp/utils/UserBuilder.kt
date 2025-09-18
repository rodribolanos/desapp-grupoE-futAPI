package ar.edu.unq.futapp.utils

import ar.edu.unq.futapp.model.User
import org.springframework.security.crypto.password.PasswordEncoder


class UserBuilder() {
    private var username: String = "testUser1"
    private var password: String = "Password123!."

    fun withUsername(username: String) = apply { this.username = username }
    fun withPassword(password: String) = apply { this.password = password }

    fun build(): User {
        return User(username, password)
    }
}
