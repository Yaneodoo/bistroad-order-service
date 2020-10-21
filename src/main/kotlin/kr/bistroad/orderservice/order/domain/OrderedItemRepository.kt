package kr.bistroad.orderservice.order.domain

import java.util.*

interface OrderedItemRepository {
    fun findById(storeId: UUID, itemId: UUID): OrderedItem?
    fun addOrderCount(storeId: UUID, itemId: UUID)
    fun subtractOrderCount(storeId: UUID, itemId: UUID)
}