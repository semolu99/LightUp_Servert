package admin.lightUp.member.dto

import admin.lightUp.common.annotation.ValidEnum
import admin.lightUp.common.status.ROLE
import admin.lightUp.member.entity.Member
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.f4b6a3.ulid.UlidCreator
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder

data class MemberDtoRequest(
    @field:NotBlank
    @JsonProperty("loginId")
    private val _loginId : String?,

    @field:NotBlank
    @field:Email
    @JsonProperty("email")
    private val _email : String?,

    @field:NotBlank
    @field:Pattern(
        regexp="^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[~!@#$%^&*()\\-=+\\[\\]{};:'\",<.>/?|])[a-zA-Z0-9~!@#$%^&*()\\-=+\\[\\]{};:'\",<.>/?|]{8,20}$",
    )
    @JsonProperty("password")
    private val _password : String?,

    @field:NotBlank
    @JsonProperty("name")
    private val _name : String?,

    @field:ValidEnum(enumClass = ROLE::class,
        message = "MEMBER 이나 PROTECTOR 중 하나를 선택해주세요")
    @JsonProperty("role")
    private val _role : ROLE,
) {
    private val encoder = SCryptPasswordEncoder(16,8,1,8,8)
    val id = UlidCreator.getUlid().toString()
    val loginId : String
        get() = _loginId!!
    val email : String
        get() = _email!!
    private val password : String
        get() =encoder.encode(_password)
    val name : String
        get() = _name!!
    val role : ROLE
        get() = ROLE.valueOf(_role.name)

    fun toEntity(): Member = Member(id, loginId, email, password, name)
}
data class LoginDto(
    @field:NotBlank
    @JsonProperty("loginId")
    private val _loginId : String?,

    @field:NotBlank
    @JsonProperty("password")
    private val _password : String?,
){
    val loginId : String
        get() = _loginId!!
    val password : String
        get() = _password!!

}
data class PasswordDto(

    @field:NotBlank
    @JsonProperty("originalPassword")
    private val _originalPassword : String?,

    @field:NotBlank
    @JsonProperty("currentPassword")//초록 밑줄은 맞춤법 그냥해라
    private val _currentPassword : String?,
)
{
    val originalPassword : String
        get() = _originalPassword!!
    val currentPassword : String
        get() = _currentPassword!!
}