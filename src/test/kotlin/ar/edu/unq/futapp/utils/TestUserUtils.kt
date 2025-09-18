package ar.edu.unq.futapp.utils

import ar.edu.unq.futapp.dto.AuthRequestDTO
import ar.edu.unq.futapp.model.User

object TestUserUtils {
    private const val USERNAME_1 = "testUser1"
    private const val PASSWORD_1 = "Password123!"
    private const val USERNAME_2 = "testUser2"
    private const val PASSWORD_2 = "Password456!"

    fun existingUser(): User = User(USERNAME_1, PASSWORD_1)

    fun existingUserAuthDto(): AuthRequestDTO = AuthRequestDTO(USERNAME_1, PASSWORD_1)

    fun invalidExistingUserAuthDto(): AuthRequestDTO = AuthRequestDTO(USERNAME_1, "WrongPassword!2")

    fun unregisteredUser(): User = User(USERNAME_2, PASSWORD_2)

    fun unregisteredUserAuthDto(): AuthRequestDTO = AuthRequestDTO(USERNAME_2, PASSWORD_2)

    fun invalidUserAuthDto(): AuthRequestDTO = AuthRequestDTO("UNEXISTING", "wrong")

    fun unexistingUserAuthDto(): AuthRequestDTO = AuthRequestDTO("UNEXISTING", "unexisting123!")

}
