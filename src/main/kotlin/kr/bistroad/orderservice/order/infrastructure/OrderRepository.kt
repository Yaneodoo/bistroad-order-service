package kr.bistroad.orderservice.order.infrastructure

import kr.bistroad.orderservice.order.domain.RequestedOrder
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Transactional
import java.util.*

interface OrderRepository : JpaRepository<RequestedOrder, UUID>, OrderRepositoryCustom {
    @Transactional
    fun removeById(id: UUID): Long
}