package admin.lightUp.member.controller

import admin.lightUp.common.authority.TokenInfo
import admin.lightUp.common.dto.BaseResponse
import admin.lightUp.common.dto.CustomUser
import admin.lightUp.member.dto.LoginDto
import admin.lightUp.member.dto.MemberDtoRequest
import admin.lightUp.member.dto.PasswordDto
import admin.lightUp.member.service.MemberService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RequestMapping("/api/v1/member")
@RestController
class MemberController(
    private val memberService: MemberService,
) {
    /**
     * 회원가입
     */
    @PostMapping("/signup")
    fun signup(@RequestBody @Valid memberDtoRequest: MemberDtoRequest) : BaseResponse<Unit> {
        val resultMsg = memberService.signUp(memberDtoRequest)
        return BaseResponse(message = resultMsg)
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
    fun changePassword(@RequestBody @Valid passwordDto: PasswordDto): String {
        val userId = (SecurityContextHolder
            .getContext()
            .authentication
            .principal as CustomUser)
            .userId
        val resultMsg= memberService.changePassword(userId,passwordDto)
        return resultMsg
    }

}