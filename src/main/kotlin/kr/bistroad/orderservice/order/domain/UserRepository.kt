package kr.bistroad.orderservice.order.domain

import java.util.*

interface UserRepository {
    fun findByIdOrNull(id: UUID): User?
}