package dev.euns.jwttemplate.global.service

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class RedisService(
    private val redisTemplate: StringRedisTemplate
) {
    fun storeRefreshToken(username: String, refreshToken: String) {
        redisTemplate.opsForValue().set("refreshToken:$username", refreshToken, 1, TimeUnit.DAYS)
    }

    fun getRefreshToken(username: String): String? {
        return redisTemplate.opsForValue().get("refreshToken:$username")
    }

    fun deleteRefreshToken(username: String) {
        redisTemplate.delete("refreshToken:$username")
    }
}