package kr.bistroad.orderservice.order

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
class OrderController(
    private val orderService: OrderService
) {
    @GetMapping("/stores/{storeId}/orders/{id}")
    fun getOrder(@PathVariable storeId: UUID, @PathVariable id: UUID) =
        orderService.readOrder(storeId, id)

    @GetMapping("/stores/{storeId}/orders")
    fun getOrders(@PathVariable storeId: UUID, @RequestParam userId: UUID?) =
        orderService.searchOrders(storeId, userId)

    @PostMapping("/stores/{storeId}/orders")
    @PreAuthorize("isAuthenticated() and (( #dto.userId == principal.userId ) or hasRole('ROLE_ADMIN'))")
    fun postOrder(@PathVariable storeId: UUID, @RequestBody dto: OrderDto.CreateReq) =
        orderService.createOrder(storeId, dto)

    @PatchMapping("/stores/{storeId}/orders/{id}")
    @PreAuthorize("isAuthenticated() and (( hasPermission(#storeId, 'Order', 'write') ) or hasRole('ROLE_ADMIN'))")
    fun patchOrder(@PathVariable storeId: UUID, @PathVariable id: UUID, @RequestBody dto: OrderDto.PatchReq) =
        orderService.patchOrder(storeId, id, dto)

    @DeleteMapping("/stores/{storeId}/orders/{id}")
    @PreAuthorize("isAuthenticated() and hasRole('ROLE_ADMIN')")
    fun deleteOrder(@PathVariable storeId: UUID, @PathVariable id: UUID) =
        orderService.deleteOrder(storeId, id)
}