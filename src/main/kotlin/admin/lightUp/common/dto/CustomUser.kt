package admin.lightUp.common.dto

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User

class CustomUser (
    val userID: String,
    name : String,
    password : String,
    authorities: Collection<GrantedAuthority>,
    ) : User (password,name,authorities)