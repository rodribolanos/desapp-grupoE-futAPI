package ar.edu.unq.futapp.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.validation.constraints.Size
import lombok.NoArgsConstructor

@Entity(name = "users")
@NoArgsConstructor
class User {
    @Id
    @Column(nullable = false, unique = true)
    private var username: String

    @Column(nullable = false)
    @field:Size(min = 8, message = "Password must be at least 8 characters long")
    private var password: String

    constructor(username: String, password: String) {
        this.username = username
        require(isValidPassword(password)) {
            "La contraseña debe tener al menos 8 caracteres, un número, un caracter especial, una mayúscula y una minúscula."
        }
        this.password = password
    }

    fun username(): String = username
    fun password(): String = password
    fun password(newPassword: String) {
        require(isValidPassword(newPassword)) {
            "La contraseña debe tener al menos 8 caracteres, un número, un caracter especial, una mayúscula y una minúscula."
        }
    }

    private fun isValidPassword(password: String): Boolean {
        val regex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{8,}")
        return regex.matches(password)
    }
}