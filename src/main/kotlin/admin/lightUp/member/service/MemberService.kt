package admin.lightUp.member.service

import admin.lightUp.common.authority.JwtTokenProvider
import admin.lightUp.common.authority.TokenInfo
import admin.lightUp.common.email.EmailUtility
import admin.lightUp.common.email.MailDto
import admin.lightUp.common.exception.InvalidInputException
import admin.lightUp.common.repository.MailRepositoryRedis
import admin.lightUp.member.dto.LoginDto
import admin.lightUp.member.dto.MemberDtoRequest
import admin.lightUp.member.entity.Member
import admin.lightUp.member.repository.MemberRepository
import jakarta.transaction.Transactional
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.stereotype.Service

@Transactional
@Service
class MemberService(
    private val memberRepository: MemberRepository,
    private val authenticationManagerBuilder: AuthenticationManagerBuilder,
    private val jwtTokenProvider: JwtTokenProvider,
    private val emailUtility: EmailUtility,
    private val mailRepositoryRedis: MailRepositoryRedis,
) {
    /**
     * 이메일 인증
     */
    fun sendMail(mailDto: MailDto): String {
        val member: Member? = memberRepository.findByEmail(mailDto.email)
        if (member != null) {
            throw InvalidInputException("이미 존재하는 이메일입니다.")
        }
        val randomString = emailUtility.sendEmail(mailDto)

        mailRepositoryRedis.saveMail(mailDto.email, randomString)

        return "메일을 성공적으로 발송했습니다."
    }
    /**
     * 이메일 검증
     */
    fun mailCheck(email: String, authCode: String): String {
        val mail = mailRepositoryRedis.findByMailCode(email)
            ?: throw InvalidInputException("발급 받은 인증 코드가 만료 되었거나 잘못 되었습니다.")
        if(authCode != mail) {
            throw InvalidInputException("발급 받은 인증 코드가 만료 되었거나 잘못 되었습니다.")
        }
        mailRepositoryRedis.deleteMailByEmail(email)
        mailRepositoryRedis.saveChecked(email)
        return "정상 확인 되었습니다."
    }
    /**
     * 회원가입
     */
    fun signUp(memberDtoRequest: MemberDtoRequest): String {
        var member : Member? = memberRepository.findByLoginId(memberDtoRequest.loginId)
        if (member != null){
            return "이미 존재하는 회원"
        }

        member = memberDtoRequest.toEntity()

        memberRepository.save(member)

        mailRepositoryRedis.deleteCheckByEmail(member.email)

        return "회원 가입 완료"
    }
    fun login(loginDto: LoginDto): TokenInfo {
        val authenticationToken = UsernamePasswordAuthenticationToken(loginDto.loginId, loginDto.password)
        val authentication = authenticationManagerBuilder.`object`.authenticate(authenticationToken)
        return jwtTokenProvider.createToken(authentication)
    }
}