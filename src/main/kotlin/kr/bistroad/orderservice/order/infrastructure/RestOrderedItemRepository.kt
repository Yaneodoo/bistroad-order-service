package kr.bistroad.orderservice.order.infrastructure

import kr.bistroad.orderservice.order.domain.OrderedItem
import kr.bistroad.orderservice.order.domain.OrderedItemRepository
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject
import org.springframework.web.client.postForObject
import java.util.*

@Component
class RestOrderedItemRepository(
    private val restTemplate: RestTemplate
) : OrderedItemRepository {
    override fun findById(storeId: UUID, itemId: UUID) =
        try {
            restTemplate.getForObject<OrderedItem>("http://store-service:8080/stores/$storeId/items/$itemId")
        } catch (ex: HttpClientErrorException.NotFound) {
            null
        }

    override fun addOrderCount(storeId: UUID, itemId: UUID) {
        restTemplate.postForObject<OrderedItem>(
            "http://store-service:8080/stores/$storeId/items/$itemId/add-order-count"
        )
    }

    override fun subtractOrderCount(storeId: UUID, itemId: UUID) {
        restTemplate.postForObject<OrderedItem>(
            "http://store-service:8080/stores/$storeId/items/$itemId/subtract-order-count"
        )
    }
}