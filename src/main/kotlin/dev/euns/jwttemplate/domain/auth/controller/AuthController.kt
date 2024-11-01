package dev.euns.jwttemplate.domain.auth.controller

import dev.euns.jwttemplate.domain.auth.dto.request.AuthLoginRequest
import dev.euns.jwttemplate.domain.auth.dto.request.AuthLogoutRequest
import dev.euns.jwttemplate.domain.auth.dto.request.AuthRefreshRequest
import dev.euns.jwttemplate.domain.auth.dto.request.AuthRegisterRequest
import dev.euns.jwttemplate.domain.auth.dto.response.AuthLoginResponse
import dev.euns.jwttemplate.domain.auth.service.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService,
) {
    @PostMapping("/login")
    fun login(@RequestBody authLoginRequest: AuthLoginRequest): AuthLoginResponse {
        return authService.login(authLoginRequest)
    }

    @PostMapping("/refresh")
    fun refresh(@RequestBody authRefreshRequest: AuthRefreshRequest): ResponseEntity<Any> {
        return authService.tokenRefresh(authRefreshRequest)
    }

    @PostMapping("/register")
    fun register(@RequestBody authRegisterRequest: AuthRegisterRequest): ResponseEntity<Any> {
        return authService.register(authRegisterRequest)
    }

    @DeleteMapping("/logout")
    fun logout(@RequestBody authLogoutRequest: AuthLogoutRequest): ResponseEntity<Any> {
        return authService.logout(authLogoutRequest)
    }
}

