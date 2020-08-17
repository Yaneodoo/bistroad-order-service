package kr.bistroad.orderservice.order

import org.springframework.stereotype.Service
import java.util.*

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val orderMapper: OrderMapper
) {
    fun createOrder(storeId: UUID, dto: OrderDto.CreateReq): OrderDto.CruRes {
        val order = Order(
            storeId = storeId,
            userId = dto.userId,
            date = dto.date,
            tableNum = dto.tableNum,
            progress = dto.progress
        )
        dto.requests
            .map {
                OrderRequest(
                    itemId = it.itemId,
                    amount = it.amount
                )
            }.forEach {
                order.addRequest(it)
            }

        orderRepository.save(order)
        return orderMapper.mapToCruRes(order)
    }

    fun readOrder(storeId: UUID, id: UUID): OrderDto.CruRes? {
        val order = orderRepository.findByStoreIdAndId(storeId, id) ?: return null
        return orderMapper.mapToCruRes(order)
    }

    fun searchOrders(storeId: UUID, userId: UUID?): List<OrderDto.CruRes> {
        val orders = if (userId == null)
            orderRepository.findAllByStoreId(storeId)
        else
            orderRepository.findAllByStoreIdAndUserId(storeId, userId)
        return orders.map(orderMapper::mapToCruRes)
    }

    fun patchOrder(storeId: UUID, id: UUID, dto: OrderDto.PatchReq): OrderDto.CruRes {
        val order = orderRepository.findByStoreIdAndId(storeId, id) ?: error("Order not found")

        if (dto.progress != null) order.progress = dto.progress

        orderRepository.save(order)
        return orderMapper.mapToCruRes(order)
    }

    fun deleteOrder(storeId: UUID, id: UUID): Boolean {
        val numDeleted = orderRepository.removeByStoreIdAndId(storeId, id)
        return numDeleted > 0
    }
}