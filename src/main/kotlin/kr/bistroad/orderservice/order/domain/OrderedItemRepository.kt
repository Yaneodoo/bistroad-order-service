package kr.bistroad.orderservice.order.domain

import java.util.*

interface OrderedItemRepository {
    fun findById(storeId: UUID, itemId: UUID): OrderedItem?
}