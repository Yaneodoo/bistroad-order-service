package kr.bistroad.orderservice.order.infrastructure

import kr.bistroad.orderservice.order.application.OrderDto
import kr.bistroad.orderservice.order.domain.Order
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.*

interface OrderRepositoryCustom {
    fun search(storeId: UUID, dto: OrderDto.SearchReq, pageable: Pageable): Page<Order>
}