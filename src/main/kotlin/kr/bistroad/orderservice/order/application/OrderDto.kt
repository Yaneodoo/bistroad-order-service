package kr.bistroad.orderservice.order.application

import kr.bistroad.orderservice.order.domain.OrderProgress
import kr.bistroad.orderservice.order.domain.OrderedItem
import kr.bistroad.orderservice.order.domain.PlacedOrder
import java.time.OffsetDateTime
import java.util.*
import kr.bistroad.orderservice.order.domain.OrderLine as DomainOrderLine
import kr.bistroad.orderservice.order.domain.Photo as DomainPhoto
import kr.bistroad.orderservice.order.domain.Store as DomainStore
import kr.bistroad.orderservice.order.domain.User as DomainUser

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
        val user: User? = null,
        val orderLines: List<OrderLine>,
        val timestamp: OffsetDateTime,
        val tableNum: Int,
        val progress: OrderProgress,
        val hasReview: Boolean
    ) : OrderDto {
        constructor(domain: PlacedOrder, domainCustomer: DomainUser?) : this(
            id = domain.id,
            store = Store(domain.store),
            userId = domain.customer.id,
            user = domainCustomer?.let(::User),
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

        data class User(
            val id: UUID,
            var username: String,
            var fullName: String,
            var phone: String,
            var role: String,
            var photo: Photo? = null
        ) {
            constructor(domain: DomainUser) : this(
                id = domain.id,
                username = domain.username,
                fullName = domain.fullName,
                phone = domain.phone,
                role = domain.role,
                photo = domain.photo?.let(::Photo)
            )
        }

        data class OrderLine(
            val item: StoreItem,
            val amount: Int
        ) {
            constructor(domain: DomainOrderLine) : this(StoreItem(domain.item), domain.amount)
        }

        data class Photo(
            val sourceUrl: String,
            val thumbnailUrl: String
        ) {
            constructor(domain: DomainPhoto) : this(domain.sourceUrl, domain.thumbnailUrl)
        }

        data class StoreItem(
            val id: UUID,
            val name: String,
            val price: Double,
            val photo: Photo? = null
        ) {
            constructor(domain: OrderedItem) : this(
                id = domain.id,
                name = domain.name,
                price = domain.price,
                photo = domain.photo?.let(::Photo)
            )
        }
    }
}