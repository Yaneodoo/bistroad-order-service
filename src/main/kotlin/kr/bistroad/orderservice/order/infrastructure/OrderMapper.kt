package kr.bistroad.orderservice.order.infrastructure

import kr.bistroad.orderservice.global.error.exception.StoreItemNotFoundException
import kr.bistroad.orderservice.global.util.typeRef
import kr.bistroad.orderservice.order.application.OrderDto
import kr.bistroad.orderservice.order.domain.Order
import org.springframework.http.HttpMethod
import org.springframework.http.RequestEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import java.net.URI
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
        try {
            restTemplate.getForObject(
                "http://store-service:8080/stores/${storeId}/items/${itemId}",
                OrderDto.CruRes.StoreItem::class.java
            )
        } catch (ex: HttpClientErrorException.NotFound) {
            throw StoreItemNotFoundException(ex)
        }

    private fun getReview(storeId: UUID, itemId: UUID, orderId: UUID): OrderDto.CruRes.Review? {
        val searchOrder = restTemplate.exchange(
            RequestEntity<List<OrderDto.CruRes.Review>>(
                HttpMethod.GET,
                URI("http://review-service:8080/stores/${storeId}/items/${itemId}/reviews?orderId=${orderId}")
            ),
            typeRef<List<OrderDto.CruRes.Review>>()
        )
        if (searchOrder.statusCode.is2xxSuccessful && !searchOrder.body.isNullOrEmpty()) {
            return searchOrder.body!!.first()
        }
        return null
    }
}