package dev.euns.jwttemplate.domain.test.controller

import dev.euns.jwttemplate.global.service.EmailService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TestController(
    private val emailService: EmailService
) {
    @GetMapping("/test")
    fun test(): String {
        return "ㅁㄹ"
    }
}