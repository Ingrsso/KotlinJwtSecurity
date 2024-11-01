package dev.euns.jwttemplate.domain.auth.dto.response

data class AuthLoginResponse(
    val accessToken: String,
    val refreshToken: String
)
