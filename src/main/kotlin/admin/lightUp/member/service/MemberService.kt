package admin.lightUp.member.service

import admin.lightUp.common.authority.JwtTokenProvider
import admin.lightUp.common.authority.TokenInfo
import admin.lightUp.common.exception.InvalidInputException
import admin.lightUp.common.email.EmailUtility
import admin.lightUp.common.repository.MailRepositoryRedis
import admin.lightUp.member.dto.LoginDto
import admin.lightUp.member.dto.MemberDtoRequest
import admin.lightUp.member.dto.CheckedDtoRequest
import admin.lightUp.member.dto.MailDto
import admin.lightUp.member.dto.PasswordDto
import admin.lightUp.member.dto.ResetPasswordDtoRequest
import admin.lightUp.member.entity.Member
import admin.lightUp.member.entity.MemberRole
import admin.lightUp.member.repository.MemberRepository
import admin.lightUp.member.repository.MemberRoleRepository
import jakarta.transaction.Transactional
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime

@Transactional
@Service
class MemberService(
    private val memberRepository: MemberRepository,
    private val authenticationManagerBuilder: AuthenticationManagerBuilder,
    private val jwtTokenProvider: JwtTokenProvider,
    private val memberRoleRepository: MemberRoleRepository,
    private val emailUtility: EmailUtility,
    private val mailRepositoryRedis: MailRepositoryRedis,
) {
    /**
     * 이메일 인증
     */
    fun sendMail(email : String): String {
        val member: Member? = memberRepository.findByEmail(email)
        if (member != null) {
            throw InvalidInputException("이미 존재하는 이메일입니다.")
        }
        val randomString = emailUtility.sendEmail(email)

        mailRepositoryRedis.saveMail(email, randomString)

        return "메일을 성공적으로 발송했습니다."
    }
    /**
     * 이메일 검증
     */
    fun checkMail(mailDto: MailDto): String {
        val mail = mailRepositoryRedis.findByMailCode(mailDto.email)
            ?: throw InvalidInputException("발급 받은 인증 코드가 만료 되었거나 잘못 되었습니다.")
        if(mailDto.authCode != mail) {
            throw InvalidInputException("발급 받은 인증 코드가 만료 되었거나 잘못 되었습니다.")
        }
        mailRepositoryRedis.deleteMailByEmail(mailDto.email)
        mailRepositoryRedis.saveChecked(mailDto.email)
        return "정상 확인 되었습니다."
    }
    /**
     * 회원가입
     */
    fun signUp(memberDtoRequest: MemberDtoRequest): String {
        val checked = mailRepositoryRedis.findByMailChecked(memberDtoRequest.loginId)

        if (checked != "Checked"){
            throw InvalidInputException("")
        }
        var member : Member? = memberRepository.findByLoginId(memberDtoRequest.loginId)
        if (member != null){
            return "이미 존재하는 회원"
        }

        member = memberDtoRequest.toEntity()
        memberRepository.save(member)

        val memberRole = MemberRole(null,member,memberDtoRequest.role)
        memberRoleRepository.save(memberRole)
        
        mailRepositoryRedis.deleteCheckByEmail(member.email)

        return "회원 가입 완료"
    }
    /**
     * 로그인
     */
    fun login(loginDto: LoginDto): TokenInfo {
        val member = memberRepository.findByLoginId(loginDto.loginId) ?: throw InvalidInputException("로그인 아이디 혹은 비밀번호가 틀림")
        val encoder= SCryptPasswordEncoder(16,8,1,8,8)
        if(!encoder.matches(loginDto.password, member.password)){
            throw InvalidInputException("로그인 아이디 혹은 비밀번호가 틀립니다.")
        }

        val authenticationToken = UsernamePasswordAuthenticationToken(loginDto.loginId, member.password)

        val authentication = authenticationManagerBuilder.`object`.authenticate(authenticationToken)
        return jwtTokenProvider.createToken(authentication)
    }

    /**
     * 비밀번호 변경
     */
    fun changePassword(userId: String,passwordDto: PasswordDto) : String{
        val member : Member = memberRepository.findMemberById(userId) ?: throw InvalidInputException("없는 아이디.")
        val encoder= SCryptPasswordEncoder(16,8,1,8,8)
        if(!encoder.matches(passwordDto.originalPassword, member.password)){
            throw InvalidInputException("로그인 아이디 혹은 비밀번호가 틀립니다.")
        }
        member.password=encoder.encode(passwordDto.currentPassword)
        member.passwordChangedData = LocalDate.now()
        memberRepository.save(member)

        return "비밀번호 변경 완료"
    }
    /**
     * 로그인 전 비밀번호 변경 전 이메일 확인
     */
    fun requestPasswordResetEmail(checkedDtoRequest: CheckedDtoRequest): String {
        val member = memberRepository.findByEmail(checkedDtoRequest.email) ?: throw InvalidInputException("없는 정보")
        if (checkedDtoRequest.loginId != member.loginId || checkedDtoRequest.name != member.name) {
            throw InvalidInputException("잘못된 정보")
        }
        val randomString = emailUtility.sendEmail(member.email)

        mailRepositoryRedis.saveMail(member.email, randomString)

        return "인증 메일을 확인 해 주세요."
    }
    /**
     * 로그인 전 비밀번호 변경 전 인증 번호 확인
     */
    fun verifyPasswordResetCode(checkedDtoRequest: CheckedDtoRequest): TokenInfo {
        val member = memberRepository.findByEmail(checkedDtoRequest.email) ?: throw InvalidInputException("잘못된 정보")
        if (checkedDtoRequest.loginId != member.loginId || checkedDtoRequest.name != member.name) {
            throw InvalidInputException("잘못된 정보")
        }
        val mail = mailRepositoryRedis.findByMailCode(checkedDtoRequest.email)
            ?: throw InvalidInputException("발급 받은 인증 코드가 만료 되었거나 잘못 되었습니다.")
        if(checkedDtoRequest.authCode != mail) {
            throw InvalidInputException("발급 받은 인증 코드가 만료 되었거나 잘못 되었습니다.")
        }

        val authenticationToken = UsernamePasswordAuthenticationToken(member.loginId, member.password)

        val authentication = authenticationManagerBuilder.`object`.authenticate(authenticationToken)

        mailRepositoryRedis.deleteMailByEmail(checkedDtoRequest.email)

        return jwtTokenProvider.createTempToken(authentication)
    }
    /**
     * 리셋 비밀번호
     */
    fun resetPassword(password:String, userId: String) : String {
        val member = memberRepository.findMemberById(userId)?: throw InvalidInputException("")
        val encoder= SCryptPasswordEncoder(16,8,1,8,8)
        member.password = encoder.encode(password)
        member.passwordChangedData = LocalDate.now()
        memberRepository.save(member)

        return "비밀번호 변경 완료"
    }
}