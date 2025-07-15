package admin.lightUp.common.email

import admin.lightUp.member.repository.MemberRepository
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import java.util.*

@Component
class EmailUtility(
    private val mailSender: JavaMailSender,
    private val memberRepository: MemberRepository
) {
    fun getRandomString(): String {
        val length = 6
        return (UUID.randomUUID().toString()).substring(0, length)
    }
    @Async
    fun sendEmail(email: String) : String {
        val randomString = getRandomString()

        val content = "LightUp 이메일 인증<br><h2>인증번호 : ${randomString}</h2><br>5분 이내로 인증코드를 입력해주세요.<br>감사합니다."

        val mimeMessage = mailSender.createMimeMessage()
        val mimeMessageHelper = MimeMessageHelper(mimeMessage, false, "UTF-8")
        mimeMessageHelper.setFrom("lightupofficial@naver.com")
        mimeMessageHelper.setTo(email)
        mimeMessageHelper.setSubject("[LightUp] 이메일 인증 메일)")
        mimeMessageHelper.setText(content, true)

        mailSender.send(mimeMessage)

        return randomString
    }
}