package kr.bistroad.orderservice.order.domain

import java.util.*

data class OrderedItem(
    val id: UUID,
    val name: String,
    val price: Double,
    val photoUri: String? = null
)