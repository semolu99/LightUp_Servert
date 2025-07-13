package admin.lightUp.member.repository

import admin.lightUp.member.entity.Member
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository: JpaRepository<Member, String> {
    fun findByLoginId(loginId: String): Member?
    fun findMemberById(userId: String): Member?
}