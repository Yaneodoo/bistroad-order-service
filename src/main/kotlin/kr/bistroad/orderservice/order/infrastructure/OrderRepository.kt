package kr.bistroad.orderservice.order.infrastructure

import kr.bistroad.orderservice.order.domain.RequestedOrder
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

interface OrderRepository : MongoRepository<RequestedOrder, UUID>, OrderRepositoryCustom {
    fun removeById(id: UUID): Long
}