package kr.bistroad.orderservice.order.application

import kr.bistroad.orderservice.order.domain.OrderProgress
import kr.bistroad.orderservice.order.domain.OrderedItem
import kr.bistroad.orderservice.order.domain.PlacedOrder
import java.time.OffsetDateTime
import java.util.*
import kr.bistroad.orderservice.order.domain.OrderLine as DomainOrderLine
import kr.bistroad.orderservice.order.domain.Store as DomainStore

interface OrderDto {
    data class ForCreate(
        val userId: UUID,
        val storeId: UUID,
        val orderLines: List<OrderLine>,
        val timestamp: OffsetDateTime = OffsetDateTime.now(),
        val tableNum: Int,
        val progress: OrderProgress
    ) : OrderDto {
        data class OrderLine(
            val itemId: UUID,
            val amount: Int
        )
    }

    data class ForUpdate(
        val progress: OrderProgress?
    ) : OrderDto

    data class ForResult(
        val id: UUID,
        val store: Store,
        val userId: UUID,
        val orderLines: List<OrderLine>,
        val timestamp: OffsetDateTime,
        val tableNum: Int,
        val progress: OrderProgress,
        val hasReview: Boolean
    ) : OrderDto {
        constructor(domain: PlacedOrder) : this(
            id = domain.id,
            store = Store(domain.store),
            userId = domain.customer.id,
            orderLines = domain.orderLines.map(ForResult::OrderLine),
            timestamp = domain.timestamp,
            tableNum = domain.tableNum,
            progress = domain.progress,
            hasReview = domain.hasReview
        )

        data class Store(
            val id: UUID,
            val ownerId: UUID,
            val name: String
        ) {
            constructor(domain: DomainStore) : this(domain.id, domain.owner.id, domain.name)
        }

        data class OrderLine(
            val item: StoreItem,
            val amount: Int
        ) {
            constructor(domain: DomainOrderLine) : this(StoreItem(domain.item), domain.amount)
        }

        data class StoreItem(
            val id: UUID,
            val name: String,
            val price: Double,
            val photoUri: String?
        ) {
            constructor(domain: OrderedItem) : this(
                id = domain.id,
                name = domain.name,
                price = domain.price,
                photoUri = domain.photoUri
            )
        }
    }
}