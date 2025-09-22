package ar.edu.unq.futapp.model

import ar.edu.unq.futapp.exception.InvalidPasswordException
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.validation.constraints.Size

@Entity(name = "users")
class User {
    @Id
    @Column(nullable = false, unique = true)
    lateinit var username: String

    @Column(nullable = false)
    @field:Size(min = 8, message = "Password must be at least 8 characters long")
    lateinit var password: String

    constructor()

    constructor(username: String, password: String) {
        this.username = username
        this.password = password
    }

    fun username(): String = username
    fun password(): String = password
    fun password(newPassword: String) {
        this.password = newPassword
    }
}