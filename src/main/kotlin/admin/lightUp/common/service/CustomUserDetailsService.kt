package admin.lightUp.common.service

import admin.lightUp.member.entity.Member
import admin.lightUp.member.repository.MemberRepository
import admin.lightUp.member.service.MemberService
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private  val memberRepository: MemberRepository,
    private  val passwordEncoder: PasswordEncoder,
): UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails =
        memberRepository.findByLoginId(username)
            ?.let { createUserDetails(it) }
            ?: throw UsernameNotFoundException("일치하는 유저를 찾을 수 없습니다.")

    private fun createUserDetails(member: Member): UserDetails =
        User(
            member.loginId,
            passwordEncoder.encode(member.password),
            member.memberRole!!.map{SimpleGrantedAuthority("ROLE_${it.role}")}
        )

}