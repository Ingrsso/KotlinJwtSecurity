package dev.euns.jwttemplate.domain.mail.service

import dev.euns.jwttemplate.domain.mail.dto.request.CheckMailRequest
import dev.euns.jwttemplate.domain.mail.dto.request.SendMailRequest
import dev.euns.jwttemplate.domain.mail.dto.response.CheckMailResponse
import dev.euns.jwttemplate.domain.mail.dto.response.SendMailResponse
import dev.euns.jwttemplate.global.service.EmailService
import dev.euns.jwttemplate.global.service.RedisService
import dev.euns.jwttemplate.global.util.JwtUtil
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.util.*


@Service
class MailService(
    private val emailService: EmailService,
    private val redisService: RedisService,
    private val jwtUtil: JwtUtil
) {
    fun sendMail(token: String,sendMailRequest: SendMailRequest): ResponseEntity<Any> {
        val emailCode: String = createCode() ?: return ResponseEntity.internalServerError().body(
            SendMailResponse(false,"이메일 코드를 만드는 도중 오류가 발생 하였습니다.")
        )
        val accessToken = token.substring(7)
        if (jwtUtil.validateToken(accessToken)) {
            if (emailService.sendEmail(sendMailRequest.email,emailCode)) {
                redisService.storeEmailVerifyCode(jwtUtil.getUsernameFromToken(accessToken), emailCode)
                return ResponseEntity.ok().body(
                    SendMailResponse(true,null)
                )
            }else{
                return ResponseEntity.internalServerError().body(
                    SendMailResponse(false,"서버에서 이메일를 보낼 수 없습니다.")
                )
            }
        }else{
            return ResponseEntity.badRequest().body(
                SendMailResponse(false,"토큰이 올바르지 않습니다.")
            )
        }
    }

    fun checkMail(token: String, checkMailRequest: CheckMailRequest): ResponseEntity<Any> {
        val accessToken = token.substring(7)
        if (jwtUtil.validateToken(accessToken)) {
            val cachedCode = redisService.getEmailVerifyCode(jwtUtil.getUsernameFromToken(accessToken))
            val requestCode = checkMailRequest.code

            if (cachedCode.equals(requestCode)) {
                return ResponseEntity.ok().body(
                    CheckMailResponse(true, null)
                )
            }else{
                return ResponseEntity.badRequest().body(
                    CheckMailResponse(false,"인증코드가 올바르지 않습니다.")
                )
            }
        }else{
            return ResponseEntity.badRequest().body(
                CheckMailResponse(false,"토큰이 올바르지 않습니다.")
            )
        }

    }

    private fun createCode(): String {
        try {
            val random: Random = SecureRandom.getInstanceStrong()
            val builder = StringBuilder()
            for (i in 0 until 6) {
                builder.append(random.nextInt(10))
            }
            return builder.toString()
        } catch (e: NoSuchAlgorithmException) {
            return ""
        }
    }
}