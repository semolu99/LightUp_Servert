package admin.lightUp.member.dto

import admin.lightUp.member.entity.Member
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.f4b6a3.ulid.UlidCreator
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

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
) {
    val id = UlidCreator.getUlid().toString()
    val loginId : String
        get() = _loginId!!
    val email : String
        get() = _email!!
    val password : String
        get() = _password!!
    val name : String
        get() = _name!!

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