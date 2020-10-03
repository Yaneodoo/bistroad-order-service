package kr.bistroad.orderservice.order.infrastructure

import kr.bistroad.orderservice.order.domain.PlacedOrder
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

interface OrderRepository : MongoRepository<PlacedOrder, UUID>, OrderRepositoryCustom {
    fun removeById(id: UUID): Long
}