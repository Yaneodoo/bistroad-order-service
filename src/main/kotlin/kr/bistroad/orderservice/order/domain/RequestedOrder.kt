package kr.bistroad.orderservice.order.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document("orders")
data class RequestedOrder(
    @Id
    val id: UUID = UUID.randomUUID(),

    val storeId: UUID,
    val userId: UUID,
    val requests: MutableList<OrderRequest> = mutableListOf(),
    val date: Date,
    val tableNum: Int,
    var progress: Progress
) {
    enum class Progress {
        REQUESTED, ACCEPTED
    }
}