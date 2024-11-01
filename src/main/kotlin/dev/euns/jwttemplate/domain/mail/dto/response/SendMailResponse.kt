package dev.euns.jwttemplate.domain.mail.dto.response

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class SendMailResponse(
    val success: Boolean,
    val message: String?
)