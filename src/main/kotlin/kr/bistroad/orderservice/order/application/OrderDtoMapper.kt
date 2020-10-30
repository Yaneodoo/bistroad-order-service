package kr.bistroad.orderservice.order.application

import kr.bistroad.orderservice.order.domain.PlacedOrder
import kr.bistroad.orderservice.order.domain.UserRepository
import org.springframework.stereotype.Component

@Component
class OrderDtoMapper(
    private val userRepository: UserRepository
) {
    fun mapToDtoForResult(domain: PlacedOrder, fetchList: List<FetchTarget> = emptyList()): OrderDto.ForResult {
        val customer = if (FetchTarget.CUSTOMER in fetchList)
            userRepository.findByIdOrNull(domain.customer.id)
        else
            null

        return OrderDto.ForResult(domain, customer)
    }
}