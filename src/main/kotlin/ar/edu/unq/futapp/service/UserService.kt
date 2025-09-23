package ar.edu.unq.futapp.service

import org.springframework.security.core.userdetails.UserDetailsService

interface UserService : UserDetailsService {
    fun register(username: String, password: String)
}

