package kr.bistroad.orderservice.order.infrastructure

import kr.bistroad.orderservice.global.util.toPage
import kr.bistroad.orderservice.order.domain.PlacedOrder
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.find
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Component
import java.util.*

@Component
class OrderRepositoryImpl(
    private val mongoTemplate: MongoTemplate
) : OrderRepositoryCustom {
    override fun search(
        customerId: UUID?,
        storeId: UUID?,
        pageable: Pageable
    ): Page<PlacedOrder> {
        val query = Query().with(pageable)

        if (storeId != null) query.addCriteria(Criteria.where("store.id").`is`(storeId))
        if (customerId != null) query.addCriteria(Criteria.where("store.customer.id").`is`(customerId))

        return mongoTemplate.find<PlacedOrder>(query).toPage(pageable)
    }
}