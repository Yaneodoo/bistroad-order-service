package kr.bistroad.orderservice.order

import kr.bistroad.orderservice.order.QOrder.order
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Component
import java.util.*

@Component
class OrderRepositoryImpl : QuerydslRepositorySupport(Order::class.java), OrderRepositoryCustom {
    override fun search(storeId: UUID, pageable: Pageable): Page<Order> {
        val query = from(order)
            .where(order.storeId.eq(storeId))

        val list = querydsl!!.applyPagination(pageable, query).fetch()
        return PageImpl(list, pageable, query.fetchCount())
    }
}