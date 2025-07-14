package admin.lightUp.member.entity

import admin.lightUp.common.status.ROLE
import jakarta.persistence.*
import java.time.LocalDate

@Entity
class Member(
    @Id
    @Column(name = "id", length = 26)
    val id: String,

    @Column(name="login_id")
    val loginId: String,

    @Column(name = "email")
    val email: String,

    @Column(name = "password")
    val password: String,

    @Column(name = "name")
    val name: String,

    @Column(name = "password_changed_date")
    @Temporal(TemporalType.DATE)
    val passwordChangedData: LocalDate = LocalDate.now(),
) {
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "member")
    val memberRole : List<MemberRole>? = null
}

@Entity
class MemberRole(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_ulid", foreignKey = ForeignKey(name = "fk_user_role_member_id"), nullable = false)
    val member: Member,

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    val role: ROLE,
)