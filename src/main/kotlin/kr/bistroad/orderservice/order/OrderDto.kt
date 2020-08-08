package kr.bistroad.orderservice.order

import java.util.*

interface OrderDto {
    data class CreateReq(
            val storeId: UUID,
            val userId: UUID,
            val requests: List<OrderRequest>,
            val date: Date = Date(),
            val tableNum: Int,
            val progress: Order.Progress
    ) {
        data class OrderRequest(
                val itemId: UUID,
                val amount: Int
        )
    }

    data class PatchReq(
            val progress: Order.Progress?
    )

    data class CruRes(
            val id: UUID,
            val storeId: UUID,
            val userId: UUID,
            val requests: List<OrderRequest>,
            val date: Date,
            val tableNum: Int,
            val progress: Order.Progress
    ) {
        data class OrderRequest(
                val item: StoreItem,
                val amount: Int,
                val review: Review?
        )

        data class StoreItem(
                val id: UUID,
                val name: String,
                val description: String,
                val price: Double,
                val photoUri: String?,
                val stars: Double
        )

        data class Review(
                val id: UUID,
                val writerId: UUID,
                val orderId: UUID,
                val contents: String,
                val stars: Int
        )
    }
}