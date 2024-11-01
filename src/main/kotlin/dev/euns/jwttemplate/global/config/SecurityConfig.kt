package dev.euns.jwttemplate.global.config

import dev.euns.jwttemplate.global.filter.JwtAuthenticationFilter
import dev.euns.jwttemplate.global.filter.JwtExceptionFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import javax.servlet.http.HttpServletResponse

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    private val jwtExceptionFilter: JwtExceptionFilter
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests { requests ->
                requests
                    .requestMatchers( "/","/swagger-ui/**", "/v3/api-docs/**").permitAll()
                    .requestMatchers("/auth/**").permitAll()
                    .anyRequest().authenticated()
            }

            .exceptionHandling { exceptions ->
                exceptions
                    .accessDeniedHandler { request, response, _ ->
                        response.status = HttpServletResponse.SC_FORBIDDEN
                        response.contentType = "application/json"
                        response.characterEncoding = "UTF-8"

                        response.outputStream.print("""{"status":403,"message":"Page Forbidden"}""")
                        response.outputStream.flush()
                    }
                    .authenticationEntryPoint { request, response, _ ->
                        response.status = HttpServletResponse.SC_UNAUTHORIZED
                        response.contentType = "application/json"
                        response.characterEncoding = "UTF-8"

                        response.outputStream.print("""{"status":401,"message":"Unauthorized Token"}""")
                        response.outputStream.flush()
                    }

            }
            .addFilterBefore(jwtExceptionFilter, UsernamePasswordAuthenticationFilter::class.java)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
        http.csrf { csrf ->
            csrf.ignoringRequestMatchers("/auth/**") // Disable CSRF for auth endpoints
        }

        return http.build()
    }

}