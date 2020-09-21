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

    fun mapToDtoForResult(entity: Order) = OrderDto.ForResult(
        id = entity.id!!,
        storeId = entity.storeId,
        userId = entity.userId,
        requests = entity.requests.map {
            OrderDto.ForResult.OrderRequest(
                item = getStoreItem(entity.storeId, it.itemId) ?: error("Item not found"),
                amount = it.amount,
                hasReview = hasReview(entity.id!!)
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
                OrderDto.ForResult.StoreItem::class.java
            )
        } catch (ex: HttpClientErrorException.NotFound) {
            throw StoreItemNotFoundException(ex)
        }

    private fun hasReview(orderId: UUID): Boolean {
        val searchOrder = restTemplate.exchange(
            RequestEntity<List<Any>>(
                HttpMethod.GET,
                URI("http://review-service:8080/reviews?orderId=$orderId")
            ),
            typeRef<List<Any>>()
        )
        if (searchOrder.statusCode.is2xxSuccessful && !searchOrder.body.isNullOrEmpty()) {
            return searchOrder.body!!.isNotEmpty()
        }
        return false
    }
}