package kr.bistroad.orderservice.order.domain

import java.util.*

interface StoreRepository {
    fun findById(id: UUID): Store?
}