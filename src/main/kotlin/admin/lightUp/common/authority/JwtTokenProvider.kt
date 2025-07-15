package admin.lightUp.common.authority


import admin.lightUp.common.dto.CustomUser
import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.*
const val TEMP_EXPIRATION_MILLIS = 1000*60 * 5
const val EXPIRATION_MILLIS = 1000*60 * 30
@Component
class JwtTokenProvider {
    @Value("\${jwt.secret}")
    lateinit var secretKey: String
    private val key by lazy {
        Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey))
    }

    /**
     * 토큰 생성
     */
    fun createToken(authentication: Authentication): TokenInfo {
        val authorities: String = authentication
            .authorities
            .joinToString(",", transform = GrantedAuthority::getAuthority)

        val now = Date()
        val accessExpiration = Date(now.time + EXPIRATION_MILLIS)

        val accessToken = Jwts.builder()
            .subject(authentication.name)
            .claim("auth", authorities)
            .claim("userId",(authentication.principal as CustomUser).userId )
            .issuedAt(now)
            .expiration(accessExpiration)
            .signWith(key, Jwts.SIG.HS256)
            .compact()

        return TokenInfo("Bearer", accessToken)
    }
    /**
     * 임시 토큰 발행
     */
    fun createTempToken(authentication: Authentication): TokenInfo {
        val now = Date()
        val accessExpiration = Date(now.time + TEMP_EXPIRATION_MILLIS)

        val tempAccessToken = Jwts.builder()
            .subject(authentication.name)
            .claim("auth", "ROLE_TEMP")
            .claim("userId",(authentication.principal as CustomUser).userId )
            .issuedAt(now)
            .expiration(accessExpiration)
            .signWith(key, Jwts.SIG.HS256)
            .compact()

        return TokenInfo("Bearer", tempAccessToken)
    }

    /**
     * 토큰 정보 추출
     */
    fun getAuthentication(token: String): Authentication {
        val claims: Claims = getClaims(token)
        val auth = claims["auth"] ?: throw RuntimeException("잘못된 토큰 입니다.") //주석 된 부분이 원래 코드들 역할 관련해서 오류난듯
      //  val authString: String = claims["auth"]?.toString() ?: ""
        val userId = claims["userId"] ?: throw RuntimeException("잘못된 토큰 입니다.")

        val authorities: Collection<GrantedAuthority> = (auth as String)
       // val authorities: Collection<GrantedAuthority> = authString//삭제 예정
            .split(",")
            .map { SimpleGrantedAuthority(it) }
           /* .map { it.trim() }             // 각 권한 문자열 앞뒤 공백 제거/삭제 예정
            .filter { it.isNotBlank() }    // 비어있거나 공백만 있는 문자열 필터링/삭제 예정
            .map { SimpleGrantedAuthority(it) }//삭제 예정*/
        val principal: UserDetails = CustomUser(userId.toString(),claims.subject, "", authorities)
        return UsernamePasswordAuthenticationToken(principal, "", authorities)
    }

    /**
     * 토큰 검증
     */
    fun validateToken(token: String): Boolean {
        try {
            getClaims(token)
            return true
        } catch (e: Exception) {
            when (e) {
                is SecurityException -> {}  // Invalid JWT Token
                is MalformedJwtException -> {}  // Invalid JWT Token
                is ExpiredJwtException -> {}    // Expired JWT Token
                is UnsupportedJwtException -> {}
                is IllegalArgumentException -> {}
                else -> {}  // else
            }
            println(e.message)
        }
        return false
    }

    private fun getClaims(token: String): Claims =
        Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload
}