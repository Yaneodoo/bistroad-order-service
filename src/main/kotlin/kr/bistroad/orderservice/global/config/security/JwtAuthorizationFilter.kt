package kr.bistroad.orderservice.global.config.security

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtAuthorizationFilter(
    authenticationManager: AuthenticationManager
) : BasicAuthenticationFilter(authenticationManager) {
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val header = request.getHeader(AUTH_HEADER)

        if (header != null && header.startsWith(JWT_PREFIX)) {
            val jwt = header.substringAfter(JWT_PREFIX)
            val authentication = UsernamePasswordAuthenticationToken(null, jwt)
            SecurityContextHolder.getContext().authentication = authentication
        }

        chain.doFilter(request, response)
    }

    companion object {
        private const val AUTH_HEADER: String = "Authorization"
        private const val JWT_PREFIX: String = "Bearer "
    }
}