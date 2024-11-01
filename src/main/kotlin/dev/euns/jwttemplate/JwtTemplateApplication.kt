package dev.euns.jwttemplate

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class JwtTemplateApplication

fun main(args: Array<String>) {
    runApplication<JwtTemplateApplication>(*args)
}
