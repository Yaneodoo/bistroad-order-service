package kr.bistroad.orderservice.order.domain

import java.util.*

data class OrderRequest(
    val itemId: UUID,
    var amount: Int
)