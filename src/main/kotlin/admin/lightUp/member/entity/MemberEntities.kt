package admin.lightUp.member.entity

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

    @Column(name = "password_changed_data")
    @Temporal(TemporalType.DATE)
    val passwordChangedData: LocalDate = LocalDate.now(),
)