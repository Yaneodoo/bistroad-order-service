package kr.bistroad.orderservice.order.infrastructure

import kr.bistroad.orderservice.order.domain.Store
import kr.bistroad.orderservice.order.domain.StoreOwner
import kr.bistroad.orderservice.order.domain.StoreRepository
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject
import java.util.*

@Component
class RestStoreRepository(
    private val restTemplate: RestTemplate
) : StoreRepository {
    override fun findById(id: UUID) =
        try {
            restTemplate.getForObject<RestStore>("http://store-service:8080/stores/$id")
                .toDomain()
        } catch (ex: HttpClientErrorException.NotFound) {
            null
        }

    data class RestStore(
        val id: UUID,
        val ownerId: UUID,
        val name: String
    ) {
        fun toDomain() = Store(
            id = id,
            owner = StoreOwner(ownerId),
            name = name
        )
    }
}