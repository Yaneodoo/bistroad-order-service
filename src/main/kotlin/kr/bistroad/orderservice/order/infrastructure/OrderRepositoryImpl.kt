package kr.bistroad.orderservice.order.infrastructure

import com.querydsl.core.BooleanBuilder
import kr.bistroad.orderservice.order.domain.QOrder.order
import kr.bistroad.orderservice.order.application.OrderDto
import kr.bistroad.orderservice.order.domain.Order
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Component

@Component
class OrderRepositoryImpl : QuerydslRepositorySupport(Order::class.java), OrderRepositoryCustom {
    override fun search(dto: OrderDto.SearchReq, pageable: Pageable): Page<Order> {
        val booleanBuilder = BooleanBuilder()

        if (dto.storeId != null) booleanBuilder.and(order.storeId.eq(dto.storeId))
        if (dto.userId != null) booleanBuilder.and(order.userId.eq(dto.userId))

        val query = from(order)
            .where(booleanBuilder)

        val list = querydsl!!.applyPagination(pageable, query).fetch()
        return PageImpl(list, pageable, query.fetchCount())
    }
}