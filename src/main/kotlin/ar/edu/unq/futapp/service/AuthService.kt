package ar.edu.unq.futapp.service

import ar.edu.unq.futapp.model.AuthRequest
import ar.edu.unq.futapp.model.AuthResponse
import ar.edu.unq.futapp.model.RefreshRequest

interface AuthService {
    fun login(request: AuthRequest): AuthResponse
    fun register(request: AuthRequest): AuthResponse
    fun refresh(request: RefreshRequest): AuthResponse
}