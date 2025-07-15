package admin.lightUp.common.service

import admin.lightUp.common.dto.BaseResponse
import admin.lightUp.common.dto.CustomUser
import admin.lightUp.common.exception.InvalidInputException
import admin.lightUp.common.status.ResultCode
import admin.lightUp.member.entity.Member
import admin.lightUp.member.repository.MemberRepository
import admin.lightUp.member.service.MemberService
import jakarta.transaction.Transactional
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
@Transactional
class CustomUserDetailsService(
    private  val memberRepository: MemberRepository,
    private  val passwordEncoder: PasswordEncoder,
): UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails =
        memberRepository.findByLoginId(username)
            ?.let { createUserDetails(it) }
            ?: throw InvalidInputException(ResultCode.NOT_MEMBER.statusCode, ResultCode.NOT_MEMBER.message, ResultCode.NOT_MEMBER.code)

    private fun createUserDetails(member: Member): UserDetails =
        CustomUser(
            member.id,
            member.name,
            passwordEncoder.encode(member.password),
            member.memberRole!!.map{SimpleGrantedAuthority("ROLE_${it.role}")}
        )

}