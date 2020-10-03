package kr.bistroad.orderservice.order.domain

import java.util.*

data class Store(
    val id: UUID,
    val owner: StoreOwner
)