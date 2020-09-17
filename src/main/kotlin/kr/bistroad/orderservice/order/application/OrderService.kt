package kr.bistroad.orderservice.order.application

import kr.bistroad.orderservice.global.config.security.UserPrincipal
import kr.bistroad.orderservice.global.error.exception.OrderNotFoundException
import kr.bistroad.orderservice.order.domain.Order
import kr.bistroad.orderservice.order.domain.OrderRequest
import kr.bistroad.orderservice.order.infrastructure.OrderMapper
import kr.bistroad.orderservice.order.infrastructure.OrderRepository
import kr.bistroad.orderservice.order.infrastructure.StoreService
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.util.*

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val orderMapper: OrderMapper,
    private val storeService: StoreService
) {
    fun createOrder(dto: OrderDto.CreateReq): OrderDto.CruRes {
        val order = Order(
            storeId = dto.storeId,
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

    fun readOrder(id: UUID): OrderDto.CruRes? {
        val order = orderRepository.findByIdOrNull(id) ?: return null
        return orderMapper.mapToCruRes(order)
    }

    fun searchOrders(dto: OrderDto.SearchReq, pageable: Pageable): List<OrderDto.CruRes> {
        return orderRepository.search(dto, pageable)
            .content.map(orderMapper::mapToCruRes)
    }

    fun patchOrder(id: UUID, dto: OrderDto.PatchReq): OrderDto.CruRes {
        val order = orderRepository.findByIdOrNull(id) ?: throw OrderNotFoundException()
        val store = storeService.getStore(order.storeId) ?: throw IllegalStateException("Store not found")

        val principal = UserPrincipal.ofCurrentContext()
        if (principal.userId != store.ownerId && !principal.isAdmin) throw AccessDeniedException("No permission")

        if (dto.progress != null) order.progress = dto.progress

        orderRepository.save(order)
        return orderMapper.mapToCruRes(order)
    }

    fun deleteOrder(id: UUID): Boolean {
        val numDeleted = orderRepository.removeById(id)
        return numDeleted > 0
    }
}