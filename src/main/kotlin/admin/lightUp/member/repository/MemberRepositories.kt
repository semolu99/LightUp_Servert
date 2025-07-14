package admin.lightUp.member.repository

import admin.lightUp.member.entity.Member
import admin.lightUp.member.entity.MemberRole
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository: JpaRepository<Member, String> {
    fun findByLoginId(loginId: String): Member?
    fun findMemberById(userId: String): Member?
    fun findByEmail(email: String) : Member?
}
interface MemberRoleRepository : JpaRepository<MemberRole, Long>{
}