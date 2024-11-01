package dev.euns.jwttemplate.domain.auth.dto.response

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class AuthLogoutResponse(
    val success: Boolean,
    val message: String?
)