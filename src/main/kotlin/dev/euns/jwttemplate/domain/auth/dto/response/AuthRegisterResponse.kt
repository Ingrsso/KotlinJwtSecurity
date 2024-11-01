package dev.euns.jwttemplate.domain.auth.dto.response

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class AuthRegisterResponse(
    val success: Boolean,
    val message: String?
)