package ar.edu.unq.futapp.model

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String
)