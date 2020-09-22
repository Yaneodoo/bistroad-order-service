package kr.bistroad.orderservice.order.application

import kr.bistroad.orderservice.order.domain.RequestedOrder
import java.util.*

interface OrderDto {
    data class ForCreate(
        val id: UUID? = null,
        val userId: UUID,
        val storeId: UUID,
        val requests: List<OrderRequest>,
        val date: Date = Date(),
        val tableNum: Int,
        val progress: RequestedOrder.Progress
    ) : OrderDto {
        data class OrderRequest(
            val itemId: UUID,
            val amount: Int
        )
    }

    data class ForUpdate(
        val progress: RequestedOrder.Progress?
    ) : OrderDto

    data class ForResult(
        val id: UUID,
        val storeId: UUID,
        val userId: UUID,
        val requests: List<OrderRequest>,
        val date: Date,
        val tableNum: Int,
        val progress: RequestedOrder.Progress
    ) : OrderDto {
        data class OrderRequest(
            val item: StoreItem,
            val amount: Int,
            val hasReview: Boolean
        )

        data class StoreItem(
            val id: UUID,
            val name: String,
            val description: String,
            val price: Double,
            val photoUri: String?,
            val stars: Double
        )
    }
}