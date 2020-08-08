package kr.bistroad.orderservice.order

import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject
import java.util.*

@Component
class OrderMapper {
    private val restTemplate: RestTemplate = RestTemplate()

    fun mapToCruRes(entity: Order) = OrderDto.CruRes(
            id = entity.id!!,
            storeId = entity.storeId,
            userId = entity.userId,
            requests = entity.requests.map {
                OrderDto.CruRes.OrderRequest(
                        item = getStoreItem(entity.storeId, it.itemId) ?: error("Item not found"),
                        amount = it.amount,
                        review = getReview(entity.storeId, it.itemId, entity.id!!)
                )
            },
            date = entity.date,
            tableNum = entity.tableNum,
            progress = entity.progress
    )

    private fun getStoreItem(storeId: UUID, itemId: UUID) =
            restTemplate.getForObject(
                    "http://store-service:8080/stores/${storeId}/items/${itemId}",
                    OrderDto.CruRes.StoreItem::class.java
            )

    private fun getReview(storeId: UUID, itemId: UUID, orderId: UUID) =
            restTemplate.getForObject(
                    "http://store-service:8080/stores/${storeId}/items/${itemId}/reviews?orderId=${orderId}",
                    OrderDto.CruRes.Review::class.java
            )
}