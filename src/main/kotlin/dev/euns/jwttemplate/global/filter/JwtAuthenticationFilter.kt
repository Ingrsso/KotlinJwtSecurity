package dev.euns.jwttemplate.global.filter

import dev.euns.jwttemplate.global.service.RedisService
import dev.euns.jwttemplate.global.util.JwtUtil
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class JwtAuthenticationFilter(
    private val jwtUtil: JwtUtil,
    private val redisService: RedisService

) : OncePerRequestFilter() {

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: jakarta.servlet.http.HttpServletRequest,
        response: jakarta.servlet.http.HttpServletResponse,
        filterChain: jakarta.servlet.FilterChain,
    ) {
        val authHeader = request.getHeader("Authorization")
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            val token = authHeader.substring(7)
            try {
                if (jwtUtil.validateToken(token)) {
                    val username = jwtUtil.getUsernameFromToken(token)
                    val cacheRefreshToken = redisService.getRefreshToken(username)
                    if (cacheRefreshToken != null && cacheRefreshToken == token) {
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Try use refresh token")
                        return
                    }

                    val authToken = UsernamePasswordAuthenticationToken(username, null, null)
                    authToken.details = WebAuthenticationDetailsSource().buildDetails(request)


                    SecurityContextHolder.getContext().authentication = authToken
                }
            } catch (ex: Exception) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token: ${ex.message}")
                return
            }
        }
        filterChain.doFilter(request, response)
    }
}