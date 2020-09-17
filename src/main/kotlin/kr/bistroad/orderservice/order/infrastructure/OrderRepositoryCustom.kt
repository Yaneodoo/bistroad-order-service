package kr.bistroad.orderservice.order.infrastructure

import kr.bistroad.orderservice.order.application.OrderDto
import kr.bistroad.orderservice.order.domain.Order
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface OrderRepositoryCustom {
    fun search(dto: OrderDto.SearchReq, pageable: Pageable): Page<Order>
}