package kr.bistroad.orderservice.order

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Transactional
import java.util.*

interface OrderRepository : JpaRepository<Order, UUID>, OrderRepositoryCustom {
    fun findByStoreIdAndId(storeId: UUID, id: UUID): Order?
    fun findAllByStoreId(storeId: UUID): List<Order>
    fun findAllByStoreIdAndUserId(storeId: UUID, userId: UUID): List<Order>

    @Transactional
    fun removeByStoreIdAndId(storeId: UUID, id: UUID): Long
}