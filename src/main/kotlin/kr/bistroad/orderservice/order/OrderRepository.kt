package kr.bistroad.orderservice.order

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Transactional
import java.util.*

interface OrderRepository : JpaRepository<Order, UUID>, OrderRepositoryCustom {
    fun findByStoreIdAndId(storeId: UUID, id: UUID): Order?

    @Transactional
    fun removeByStoreIdAndId(storeId: UUID, id: UUID): Long
}