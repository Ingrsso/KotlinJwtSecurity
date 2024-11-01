package dev.euns.jwttemplate.global.service
import jakarta.transaction.Transactional
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
@Transactional
class EmailService(
    private val javaMailSender: JavaMailSender,
) {

    @Async
    fun sendEmail(email: String?, code:String): Boolean {
        val mimeMessage = javaMailSender.createMimeMessage()
        try {
            val mimeMessageHelper = MimeMessageHelper(mimeMessage, false, "UTF-8")
            mimeMessageHelper.setTo(email!!)
            mimeMessageHelper.setSubject("테스트 페이지 인증코드 입니다.")
            mimeMessageHelper.setText("귀하의 인증코드는 다음과 같습니다. $code", false)
            javaMailSender.send(mimeMessage)

            return true
        } catch (e: Exception) {
            return false
        }
    }
}
