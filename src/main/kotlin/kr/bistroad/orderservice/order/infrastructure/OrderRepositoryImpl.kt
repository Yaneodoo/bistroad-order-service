package kr.bistroad.orderservice.order.infrastructure

import kr.bistroad.orderservice.global.util.toPage
import kr.bistroad.orderservice.order.domain.RequestedOrder
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.find
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.where
import org.springframework.stereotype.Component
import java.util.*

@Component
class OrderRepositoryImpl(
    private val mongoTemplate: MongoTemplate
) : OrderRepositoryCustom {
    override fun search(
        userId: UUID?,
        storeId: UUID?,
        pageable: Pageable
    ): Page<RequestedOrder> {
        val query = Query().with(pageable)

        if (storeId != null) query.addCriteria(where(RequestedOrder::storeId).`is`(storeId))
        if (userId != null) query.addCriteria(where(RequestedOrder::userId).`is`(userId))

        return mongoTemplate.find<RequestedOrder>(query).toPage(pageable)
    }
}