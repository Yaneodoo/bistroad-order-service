package kr.bistroad.orderservice.order.infrastructure

import com.querydsl.core.BooleanBuilder
import kr.bistroad.orderservice.order.domain.Order
import kr.bistroad.orderservice.order.domain.QOrder.order
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Component
import java.util.*

@Component
class OrderRepositoryImpl : QuerydslRepositorySupport(Order::class.java), OrderRepositoryCustom {
    override fun search(
        userId: UUID?,
        storeId: UUID?,
        pageable: Pageable
    ): Page<Order> {
        val booleanBuilder = BooleanBuilder()

        if (storeId != null) booleanBuilder.and(order.storeId.eq(storeId))
        if (userId != null) booleanBuilder.and(order.userId.eq(userId))

        val query = from(order)
            .where(booleanBuilder)

        val list = querydsl!!.applyPagination(pageable, query).fetch()
        return PageImpl(list, pageable, query.fetchCount())
    }
}