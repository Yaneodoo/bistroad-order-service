package kr.bistroad.orderservice.order.infrastructure

import kr.bistroad.orderservice.order.domain.PlacedOrder
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.*

interface OrderRepositoryCustom {
    fun search(
        customerId: UUID?,
        storeId: UUID?,
        pageable: Pageable
    ): Page<PlacedOrder>
}