package dev.euns.jwttemplate.domain.auth.dto.request

data class AuthRegisterRequest(
    val username: String,
    val password: String
)
