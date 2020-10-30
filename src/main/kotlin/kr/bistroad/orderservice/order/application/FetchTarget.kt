package kr.bistroad.orderservice.order.application

enum class FetchTarget(
    private val value: String
) {
    CUSTOMER("customer");

    companion object {
        fun from(value: String) = values().first { it.value == value }
    }
}