package kr.bistroad.orderservice.order.presentation

import kr.bistroad.orderservice.order.application.OrderDto
import kr.bistroad.orderservice.order.domain.RequestedOrder
import java.util.*

interface OrderRequest {
    data class SearchParams(
        val userId: UUID?,
        val storeId: UUID?
    )

    data class PostBody(
        val userId: UUID,
        val storeId: UUID,
        val requests: List<Request>,
        val date: Date = Date(),
        val tableNum: Int,
        val progress: RequestedOrder.Progress
    ) {
        fun toDtoForCreate() = OrderDto.ForCreate(
            userId = userId,
            storeId = storeId,
            requests = requests.map { it.toDtoOrderRequest() },
            date = date,
            tableNum = tableNum,
            progress = progress
        )

        data class Request(
            val itemId: UUID,
            val amount: Int
        ) {
            fun toDtoOrderRequest() = OrderDto.ForCreate.OrderRequest(
                itemId = itemId,
                amount = amount
            )
        }
    }

    data class PatchBody(
        val progress: RequestedOrder.Progress?
    ) {
        fun toDtoForUpdate() = OrderDto.ForUpdate(
            progress = progress
        )
    }
}