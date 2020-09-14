package kr.bistroad.orderservice.global.config.security

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class AuthorizationFilter(
    authenticationManager: AuthenticationManager
) : BasicAuthenticationFilter(authenticationManager) {
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val userId = request.getHeader(HEADER_AUTHORIZATION_USER_ID)?.let { UUID.fromString(it) }
        val role = request.getHeader(HEADER_AUTHORIZATION_ROLE)

        val authentication = UsernamePasswordAuthenticationToken(
            UserPrincipal(userId, role),
            null,
            if (role != null) listOf(SimpleGrantedAuthority(role)) else emptyList()
        )
        SecurityContextHolder.getContext().authentication = authentication

        chain.doFilter(request, response)
    }

    companion object {
        const val HEADER_AUTHORIZATION_USER_ID = "Authorization-User-Id"
        const val HEADER_AUTHORIZATION_ROLE = "Authorization-Role"
    }
}