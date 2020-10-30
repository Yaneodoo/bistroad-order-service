package kr.bistroad.orderservice.order.application

import kr.bistroad.orderservice.global.config.security.UserPrincipal
import kr.bistroad.orderservice.global.error.exception.OrderNotFoundException
import kr.bistroad.orderservice.global.error.exception.StoreItemNotFoundException
import kr.bistroad.orderservice.global.error.exception.StoreNotFoundException
import kr.bistroad.orderservice.order.domain.*
import kr.bistroad.orderservice.order.infrastructure.OrderRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import java.util.*

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val orderedItemRepository: OrderedItemRepository,
    private val storeRepository: StoreRepository,
    private val orderDtoMapper: OrderDtoMapper
) {
    fun createOrder(dto: OrderDto.ForCreate): OrderDto.ForResult {
        val store = storeRepository.findById(dto.storeId)
            ?: throw StoreNotFoundException()
        val orderLines = dto.orderLines.map {
            val item = orderedItemRepository.findById(dto.storeId, it.itemId)
                ?: throw StoreItemNotFoundException()
            OrderLine(item = item, amount = it.amount)
        }

        val order = PlacedOrder(
            store = store,
            customer = Customer(dto.userId),
            orderLines = orderLines,
            timestamp = dto.timestamp,
            tableNum = dto.tableNum,
            progress = dto.progress
        )
        orderRepository.save(order)

        for (orderLine in orderLines) {
            orderedItemRepository.addOrderCount(store.id, orderLine.item.id)
        }

        return orderDtoMapper.mapToDtoForResult(order)
    }

    fun readOrder(id: UUID, fetchList: List<FetchTarget> = emptyList()): OrderDto.ForResult? {
        val order = orderRepository.findByIdOrNull(id) ?: return null
        return orderDtoMapper.mapToDtoForResult(order, fetchList)
    }

    fun searchOrders(
        userId: UUID?,
        storeId: UUID?,
        pageable: Pageable,
        fetchList: List<FetchTarget> = emptyList()
    ): List<OrderDto.ForResult> =
        orderRepository.search(
            customerId = userId,
            storeId = storeId,
            pageable = pageable
        ).content
            .map { orderDtoMapper.mapToDtoForResult(it, fetchList) }

    fun updateOrder(id: UUID, dto: OrderDto.ForUpdate): OrderDto.ForResult {
        val order = orderRepository.findByIdOrNull(id) ?: throw OrderNotFoundException()

        val principal = UserPrincipal.ofCurrentContext()
        if (principal.userId != order.store.owner.id && !principal.isAdmin) throw AccessDeniedException("No permission")

        if (dto.progress != null) order.progress = dto.progress

        orderRepository.save(order)
        return orderDtoMapper.mapToDtoForResult(order)
    }

    fun deleteOrder(id: UUID): Boolean {
        val order = orderRepository.findByIdOrNull(id) ?: throw OrderNotFoundException()

        for (orderLine in order.orderLines) {
            orderedItemRepository.subtractOrderCount(order.store.id, orderLine.item.id)
        }

        val numDeleted = orderRepository.removeById(id)
        return numDeleted > 0
    }

    fun addReview(id: UUID, reviewId: UUID): OrderDto.ForResult {
        val order = orderRepository.findByIdOrNull(id) ?: throw OrderNotFoundException()

        order.reviews += Review(reviewId)
        orderRepository.save(order)

        return orderDtoMapper.mapToDtoForResult(order)
    }

    fun removeReview(id: UUID, reviewId: UUID): OrderDto.ForResult {
        val order = orderRepository.findByIdOrNull(id) ?: throw OrderNotFoundException()

        order.reviews -= Review(reviewId)
        orderRepository.save(order)

        return orderDtoMapper.mapToDtoForResult(order)
    }
}