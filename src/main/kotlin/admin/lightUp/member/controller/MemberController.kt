package admin.lightUp.member.controller

import admin.lightUp.common.authority.TokenInfo
import admin.lightUp.common.dto.BaseResponse
import admin.lightUp.common.dto.CustomUser
import admin.lightUp.member.dto.CheckedDtoRequest
import admin.lightUp.member.dto.LoginDto
import admin.lightUp.member.dto.MailDto
import admin.lightUp.member.dto.MemberDtoRequest
import admin.lightUp.member.dto.PasswordDto
import admin.lightUp.member.dto.ResetPasswordDtoRequest
import admin.lightUp.member.service.MemberService
import jakarta.validation.Valid
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RequestMapping("/api/v1/member")
@RestController
class MemberController(
    private val memberService: MemberService,
) {
    /**
     * 이메일 인증
     */
    @PostMapping("/mail")
    fun sendMail(@RequestBody @Valid mailDto: MailDto) : BaseResponse<Unit> {
        val resultMsg = memberService.sendMail(mailDto.email)
        return BaseResponse(statusMessage = resultMsg)
    }
    /**
     * 이메일 검증
     */
    @PostMapping("/mail/check")
    fun checkMail(@RequestBody @Valid mailDto: MailDto): BaseResponse<Unit> {
        val resultMsg = memberService.checkMail(mailDto)
        return BaseResponse(statusMessage = resultMsg)
    }
    /**
     * 회원가입
     */
    @PostMapping("/signup")
    fun signup(@RequestBody @Valid memberDtoRequest: MemberDtoRequest) : BaseResponse<Unit> {
        val resultMsg = memberService.signUp(memberDtoRequest)
        return BaseResponse(statusMessage = resultMsg)
    }
    /**
     * 로그인
     */
    @PostMapping("/login")
    fun login(@RequestBody @Valid loginDto: LoginDto): BaseResponse<TokenInfo> {
        val tokenInfo = memberService.login(loginDto)
        return BaseResponse(data = tokenInfo)
    }
    /**
     * 비밀번호 변경
     */
    @PutMapping("/change")
    fun changePassword(@RequestBody @Valid passwordDto: PasswordDto): BaseResponse<Unit>{
        val userId = (SecurityContextHolder
            .getContext()
            .authentication
            .principal as CustomUser)
            .userId
        val resultMsg= memberService.changePassword(userId,passwordDto)
        return BaseResponse(statusMessage = resultMsg)
    }
    /**
     * 로그인 전 비밀번호 변경 전 이메일 확인
     */
    @PostMapping("/reset/check")
    fun requestPasswordResetEmail(@RequestBody @Valid checkedDtoRequest: CheckedDtoRequest): BaseResponse<Unit> {
        val resultMsg = memberService.requestPasswordResetEmail(checkedDtoRequest)
        return BaseResponse(statusMessage = resultMsg)
    }
    /**
     * 로그인 전 비밀번호 변경 전 인증 번호 확인
     */
    @PostMapping("/reset/checked")
    fun verifyPasswordResetCode(@RequestBody @Valid checkedDtoRequest: CheckedDtoRequest): BaseResponse<TokenInfo> {
        val tokenInfo = memberService.verifyPasswordResetCode(checkedDtoRequest)
        return BaseResponse(data=tokenInfo)
    }
    /**
     * 리셋 비밀번호
     */
    @PutMapping("/reset")
    fun resetPassword(@RequestBody @Valid resetPasswordDtoRequest: ResetPasswordDtoRequest) : BaseResponse<Unit> {
        val userId = (SecurityContextHolder.getContext().authentication.principal as CustomUser).userId
        val resultMsg = memberService.resetPassword(resetPasswordDtoRequest.password, userId)
        return BaseResponse(statusMessage = resultMsg)
    }
}