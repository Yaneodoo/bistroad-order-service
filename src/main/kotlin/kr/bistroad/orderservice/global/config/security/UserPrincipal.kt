package kr.bistroad.orderservice.global.config.security

import java.util.*

data class UserPrincipal(
    val userId: UUID,
    val role: UserRole
) {
    enum class UserRole {
        ROLE_USER, ROLE_STORE_OWNER, ROLE_ADMIN
    }
}