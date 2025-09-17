package ar.edu.unq.futapp.exception

class InvalidCredentialsException(description: String) : RuntimeException(description)
class UserAlreadyExistsException(description: String) : RuntimeException(description)
class InvalidRefreshTokenException(description: String) : RuntimeException(description)

