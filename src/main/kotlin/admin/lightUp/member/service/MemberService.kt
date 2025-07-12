package admin.lightUp.member.service

import admin.lightUp.member.dto.MemberDtoRequest
import admin.lightUp.member.entity.Member
import admin.lightUp.member.repository.MemberRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Transactional
@Service
class MemberService(
    private val memberRepository: MemberRepository,
) {
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

        return "회원 가입 완료"
    }
}