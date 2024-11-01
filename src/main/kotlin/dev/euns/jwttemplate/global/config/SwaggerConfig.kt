package dev.euns.jwttemplate.global.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class SwaggerConfig {
    var securityScheme: SecurityScheme = SecurityScheme()
        .type(SecurityScheme.Type.HTTP)
        .scheme("Bearer")
        .bearerFormat("JWT")
        .`in`(SecurityScheme.In.HEADER)
        .name("Authorization")

    var securityRequirement: SecurityRequirement = SecurityRequirement().addList("BearerAuth")

    @Bean
    fun openAPI(): OpenAPI {
        return OpenAPI()
            .components(Components())
            .info(apiInfo())
            .addSecurityItem(securityRequirement)
            .schemaRequirement("BearerAuth", securityScheme)
    }

    private fun apiInfo(): Info {
        return Info()
            .title("Login JWT API DOCS")
            .description("Login JWT API DOCS Kotlin")
            .version("1.0.1")
    }
}