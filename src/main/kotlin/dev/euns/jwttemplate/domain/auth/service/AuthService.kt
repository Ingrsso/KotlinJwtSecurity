package dev.euns.jwttemplate.domain.auth.service

import dev.euns.jwttemplate.domain.auth.dto.request.AuthLoginRequest
import dev.euns.jwttemplate.domain.auth.dto.request.AuthLogoutRequest
import dev.euns.jwttemplate.domain.auth.dto.request.AuthRefreshRequest
import dev.euns.jwttemplate.domain.auth.dto.request.AuthRegisterRequest
import dev.euns.jwttemplate.domain.auth.dto.response.AuthLoginResponse
import dev.euns.jwttemplate.domain.auth.dto.response.AuthLogoutResponse
import dev.euns.jwttemplate.domain.auth.dto.response.AuthRegisterResponse
import dev.euns.jwttemplate.domain.auth.entity.User
import dev.euns.jwttemplate.domain.auth.repository.UserRepository
import dev.euns.jwttemplate.global.dto.ErrorResponse
import dev.euns.jwttemplate.global.service.RedisService
import dev.euns.jwttemplate.global.util.JwtUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service


@Service
class AuthService(
    private val jwtUtil: JwtUtil,
    private val redisService: RedisService,
    @Autowired
    private val userRepository: UserRepository
) {
    fun login(authLoginRequest: AuthLoginRequest):  ResponseEntity<Any> {
        val encoder = BCryptPasswordEncoder()
        val user: List<User> = userRepository.findByUsername(authLoginRequest.username)
        if (user.isNotEmpty()) {
            if (encoder.matches(authLoginRequest.password, user[0].password)) {
                val refreshToken:String = jwtUtil.generateRefreshToken(username = authLoginRequest.username)
                redisService.storeRefreshToken(authLoginRequest.username, refreshToken)
                return ResponseEntity.ok(AuthLoginResponse(
                    accessToken = jwtUtil.generateToken(username = authLoginRequest.username),
                    refreshToken = refreshToken
                ))
            }else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse(400, "비밀번호가 올바르지 않습니다."))
            }
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse(400, "아이디가 올바르지 않습니다."))

        }
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
    fun register(authRegisterRequest: AuthRegisterRequest) : ResponseEntity<Any> {
        val findUserByUsername = userRepository.findByUsername(authRegisterRequest.username)
        return if (findUserByUsername.isEmpty()) {
            val encoder = BCryptPasswordEncoder()
            val user = User().apply {
                username = authRegisterRequest.username
                password = encoder.encode(authRegisterRequest.password)
            }
            userRepository.save(user)
            ResponseEntity.ok(
                AuthRegisterResponse(
                    success = true,
                    message = null
                )
            )
        }else{
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse(400, "이미 존재하는 유저 입니다."))
        }
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