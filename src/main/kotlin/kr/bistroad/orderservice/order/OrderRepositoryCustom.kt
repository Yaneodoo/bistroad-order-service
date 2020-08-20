package kr.bistroad.orderservice.order

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.*

interface OrderRepositoryCustom {
    fun search(storeId: UUID, pageable: Pageable): Page<Order>
}