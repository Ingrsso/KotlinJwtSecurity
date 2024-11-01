package dev.euns.jwttemplate.global.filter

import com.auth0.jwt.exceptions.TokenExpiredException
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

import org.springframework.http.HttpStatus
import org.slf4j.LoggerFactory

import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.servlet.NoHandlerFoundException
import java.io.IOException
import javax.naming.AuthenticationException

data class ErrorResponse(val status: Int, val message: String)

@Component
class JwtExceptionFilter : OncePerRequestFilter() {

    private val objectMapper = ObjectMapper() // Create an ObjectMapper instance

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain
    ) {
        try {
            chain.doFilter(request, response) // Proceed with the filter chain
        } catch (ex: Exception) {
            logger.error("Exception caught in CustomExceptionFilter: ${ex.message}", ex) // Log the exception
            handleException(response, ex) // Handle the exception
        }
    }

    private fun handleException(response: HttpServletResponse, ex: Exception) {
        val errorResponse = when (ex) {
            is AuthenticationException -> ErrorResponse(HttpStatus.UNAUTHORIZED.value(), "Authentication failed: ${ex.message}")
            is NoHandlerFoundException -> ErrorResponse(HttpStatus.NOT_FOUND.value(), "Resource not found")
            else -> ErrorResponse(HttpStatus.FORBIDDEN.value(), "Access denied: ${ex.message}")
        }

        response.contentType = "application/json"
        response.status = errorResponse.status
        response.outputStream.print(objectMapper.writeValueAsString(errorResponse)) // Write JSON response
        response.outputStream.flush()
    }
}