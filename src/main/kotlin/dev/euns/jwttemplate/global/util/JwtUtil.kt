package dev.euns.jwttemplate.global.util

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.springframework.stereotype.Component
import java.util.Date

@Component
class JwtUtil {

    private val secret = "your_secret_key" // Use a strong secret key
    private val expirationTime = 60 * 60 * 1000 // 1 hour
    private val expirationRefreshTime = 60 * 60 * 1000 * 24

    fun generateToken(username: String): String {
        return JWT.create()
            .withSubject(username)
            .withExpiresAt(Date(System.currentTimeMillis() + expirationTime))
            .sign(Algorithm.HMAC256(secret))
    }

    fun generateRefreshToken(username: String): String {
        return JWT.create()
            .withSubject(username)
            .withExpiresAt(Date(System.currentTimeMillis() + expirationRefreshTime))
            .sign(Algorithm.HMAC256(secret))
    }

    fun validateToken(token: String): Boolean {
        return try {
            JWT.require(Algorithm.HMAC256(secret))
                .build()
                .verify(token)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getUsernameFromToken(token: String): String {
        return JWT.decode(token).subject
    }
}
