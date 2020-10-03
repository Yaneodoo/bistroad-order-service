package kr.bistroad.orderservice.order.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.OffsetDateTime
import java.util.*

@Document("placedOrders")
data class PlacedOrder(
    @Id
    val id: UUID = UUID.randomUUID(),

    val store: Store,
    val customer: Customer,
    val orderLines: List<OrderLine> = listOf(),
    val timestamp: OffsetDateTime,
    val tableNum: Int,
    var progress: OrderProgress,
    val reviews: MutableList<Review> = mutableListOf()
) {
    val hasReview: Boolean
        get() = reviews.isNotEmpty()
}