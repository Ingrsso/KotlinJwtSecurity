package dev.euns.jwttemplate.domain.auth.service

import dev.euns.jwttemplate.domain.auth.dto.request.AuthLoginRequest
import dev.euns.jwttemplate.domain.auth.dto.request.AuthLogoutRequest
import dev.euns.jwttemplate.domain.auth.dto.request.AuthRefreshRequest
import dev.euns.jwttemplate.domain.auth.dto.request.AuthRegisterRequest
import dev.euns.jwttemplate.domain.auth.dto.response.AuthLoginResponse
import dev.euns.jwttemplate.domain.auth.dto.response.AuthLogoutResponse
import dev.euns.jwttemplate.domain.auth.dto.response.AuthRegisterResponse
import dev.euns.jwttemplate.global.dto.ErrorResponse
import dev.euns.jwttemplate.global.service.RedisService
import dev.euns.jwttemplate.global.util.JwtUtil
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val jwtUtil: JwtUtil,
    private val redisService: RedisService
) {
    fun login(authLoginRequest: AuthLoginRequest): AuthLoginResponse {
        val refreshToken:String = jwtUtil.generateRefreshToken(username = authLoginRequest.username)
        redisService.storeRefreshToken(authLoginRequest.username, refreshToken)

        return AuthLoginResponse(
            accessToken = jwtUtil.generateToken(username = authLoginRequest.username),
            refreshToken = refreshToken
        )
    }
    fun logout(authLogoutRequest: AuthLogoutRequest): ResponseEntity<Any> {
        val username = jwtUtil.getUsernameFromToken(authLogoutRequest.accessToken)
        if (jwtUtil.validateToken(authLogoutRequest.accessToken)) {
            redisService.deleteRefreshToken(username)
            return ResponseEntity.ok(
               AuthLogoutResponse(
                   true,
                   null
               )
            )
        }else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorResponse(403, "존재하지 않는 토큰 입니다."))
        }
    }
    fun register(authRegisterRequest: AuthRegisterRequest): ResponseEntity<Any> {


    }
    fun tokenRefresh(authRefreshRequest: AuthRefreshRequest): ResponseEntity<Any> {
        val refreshToken = authRefreshRequest.refreshToken
        return if (jwtUtil.validateToken(refreshToken)) {
            val username: String = jwtUtil.getUsernameFromToken(refreshToken)
            val cachedToken = redisService.getRefreshToken(username)
            if (cachedToken == refreshToken) {
                val newRefreshToken: String = jwtUtil.generateRefreshToken(username)
                redisService.storeRefreshToken(username, newRefreshToken)
                ResponseEntity.ok(
                    AuthLoginResponse(
                        accessToken = jwtUtil.generateToken(username),
                        refreshToken = newRefreshToken
                    )
                )
            } else {
                ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorResponse(403, "이미 리프레시 되었거나, 변조된 토큰입니다."))
            }
        } else {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ErrorResponse(401, "만료되었거나 잘못된 토큰 입니다."))
        }
    }
}