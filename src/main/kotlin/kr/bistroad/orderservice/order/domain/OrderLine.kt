package kr.bistroad.orderservice.order.domain

data class OrderLine(
    val item: OrderedItem,
    val amount: Int
)