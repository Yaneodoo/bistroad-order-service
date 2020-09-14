package kr.bistroad.orderservice.global.config.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jws
import io.jsonwebtoken.JwtException
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.CredentialsExpiredException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component

@Component
class JwtAuthenticationProvider(
    private val jwtSigner: JwtSigner
) : AuthenticationProvider {
    private val objectMapper: ObjectMapper = ObjectMapper().registerModule(KotlinModule())

    override fun authenticate(authentication: Authentication): Authentication {
        val jws: Jws<Claims>
        try {
            jws = jwtSigner.validateToken(authentication.credentials.toString())
        } catch (ex: ExpiredJwtException) {
            throw CredentialsExpiredException(ex.message, ex)
        } catch (ex: JwtException) {
            throw BadCredentialsException(ex.message, ex)
        }

        val principal = objectMapper.readValue<UserPrincipal>(jws.body.subject)

        return UsernamePasswordAuthenticationToken(
            principal, null,
            mutableListOf(SimpleGrantedAuthority(principal.role.name))
        )
    }

    override fun supports(authentication: Class<*>) =
        authentication == UsernamePasswordAuthenticationToken::class.java
}