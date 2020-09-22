package kr.bistroad.orderservice.order.infrastructure

import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject
import java.util.*

@Component
class StoreService(
    private val restTemplate: RestTemplate
) {
    fun getStore(id: UUID) = try {
        restTemplate.getForObject<Store>("http://store-service:8080/stores/$id")
    } catch (ex: HttpClientErrorException.NotFound) {
        null
    }
}