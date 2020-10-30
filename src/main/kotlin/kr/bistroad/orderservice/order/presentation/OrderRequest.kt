package kr.bistroad.orderservice.order.presentation

import kr.bistroad.orderservice.global.error.exception.InvalidDateFormatException
import kr.bistroad.orderservice.order.application.OrderDto
import kr.bistroad.orderservice.order.domain.OrderProgress
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*

interface OrderRequest {
    data class SearchParams(
        val userId: UUID?,
        val storeId: UUID?,
        val fetch: List<String>? = null
    )

    data class PostBody(
        val userId: UUID,
        val storeId: UUID,
        val orderLines: List<OrderLine>,
        val timestamp: String? = null,
        val tableNum: Int,
        val progress: OrderProgress
    ) {
        fun toDtoForCreate() = OrderDto.ForCreate(
            userId = userId,
            storeId = storeId,
            orderLines = orderLines.map(OrderLine::toDto),
            timestamp = timestamp?.let {
                try {
                    OffsetDateTime.parse(it, DateTimeFormatter.ISO_DATE_TIME)
                } catch (ex: DateTimeParseException) {
                    throw InvalidDateFormatException(ex)
                }
            } ?: OffsetDateTime.now(),
            tableNum = tableNum,
            progress = progress
        )

        data class OrderLine(
            val itemId: UUID,
            val amount: Int
        ) {
            fun toDto() = OrderDto.ForCreate.OrderLine(
                itemId = itemId,
                amount = amount
            )
        }
    }

    data class PatchBody(
        val progress: OrderProgress?
    ) {
        fun toDtoForUpdate() = OrderDto.ForUpdate(
            progress = progress
        )
    }

    data class AddReviewBody(val reviewId: UUID)
    data class RemoveReviewBody(val reviewId: UUID)
}