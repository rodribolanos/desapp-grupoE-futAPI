package ar.edu.unq.futapp.utils.builders

import ar.edu.unq.futapp.model.User

class UserBuilder() {
    private var username: String = "testUser1"
    private var password: String = "Password123!."

    fun withUsername(username: String) = apply { this.username = username }
    fun withPassword(password: String) = apply { this.password = password }

    fun build(): User {
        return User(username, password)
    }
}