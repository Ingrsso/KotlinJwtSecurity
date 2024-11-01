package dev.euns.jwttemplate.global.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl


@Configuration
class MailConfig {
    // SMTP 서버
    @Value("\${spring.mail.host}")
    private val host: String? = null

    // 계정
    @Value("\${spring.mail.username}")
    private val username: String? = null

    // 비밀번호
    @Value("\${spring.mail.password}")
    private val password: String? = null

    // 포트번호
    @Value("\${spring.mail.port}")
    private val port = 0

    @Value("\${spring.mail.properties.mail.smtp.auth}")
    private val auth = false

    @Value("\${spring.mail.properties.mail.smtp.debug}")
    private val debug = false

    @Value("\${spring.mail.properties.mail.smtp.connectiontimeout}")
    private val connectionTimeout = 0

    @Value("\${spring.mail.properties.mail.starttls.enable}")
    private val startTlsEnable = false

    @Bean
    fun javaMailService(): JavaMailSender {
        val javaMailSender = JavaMailSenderImpl()
        javaMailSender.host = host
        javaMailSender.username = username
        javaMailSender.password = password
        javaMailSender.port = port

        val properties = javaMailSender.javaMailProperties
        properties[MAIL_SMTP_AUTH] = auth
        properties[MAIL_DEBUG] = debug
        properties[MAIL_CONNECTION_TIMEOUT] = connectionTimeout
        properties[MAIL_SMTP_STARTTLS_ENABLE] = startTlsEnable

        javaMailSender.javaMailProperties = properties
        javaMailSender.defaultEncoding = "UTF-8"

        return javaMailSender
    }

    companion object {
        private const val MAIL_SMTP_AUTH = "mail.smtp.auth"
        private const val MAIL_DEBUG = "mail.smtp.debug"
        private const val MAIL_CONNECTION_TIMEOUT = "mail.smtp.connectiontimeout"
        private const val MAIL_SMTP_STARTTLS_ENABLE = "mail.smtp.starttls.enable"
    }
}
