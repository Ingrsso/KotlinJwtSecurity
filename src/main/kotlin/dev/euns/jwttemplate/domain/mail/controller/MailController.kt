package dev.euns.jwttemplate.domain.mail.controller

import dev.euns.jwttemplate.domain.mail.dto.request.CheckMailRequest
import dev.euns.jwttemplate.domain.mail.dto.request.SendMailRequest
import dev.euns.jwttemplate.domain.mail.service.MailService
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/mail")
class MailController(
    private val mailService: MailService
) {
    @PostMapping("/send")
    fun sendMail(
        @Parameter(hidden = true)
        @RequestHeader("Authorization") authHeader: String,
        @RequestBody sendMailRequest: SendMailRequest
    ) : ResponseEntity<Any> {
        return mailService.sendMail(authHeader, sendMailRequest)
    }
    @PostMapping("/check")
    fun checkMail(
        @Parameter(hidden = true)
        @RequestHeader("Authorization") authHeader: String,
        @RequestBody checkMailRequest: CheckMailRequest
    ) : ResponseEntity<Any> {
        return mailService.checkMail(authHeader, checkMailRequest)
    }
}