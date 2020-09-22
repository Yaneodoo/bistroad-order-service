package kr.bistroad.orderservice.order.presentation

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import kr.bistroad.orderservice.global.error.exception.OrderNotFoundException
import kr.bistroad.orderservice.order.application.OrderService
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@Api(tags = ["/orders"])
class OrderController(
    private val orderService: OrderService
) {
    @GetMapping("/orders/{id}")
    @ApiOperation("\${swagger.doc.operation.order.get-order.description}")
    fun getOrder(@PathVariable id: UUID) =
        orderService.readOrder(id) ?: throw OrderNotFoundException()

    @GetMapping("/orders")
    @ApiOperation("\${swagger.doc.operation.order.get-orders.description}")
    fun getOrders(params: OrderRequest.SearchParams, pageable: Pageable) =
        orderService.searchOrders(
            userId = params.userId,
            storeId = params.storeId,
            pageable = pageable
        )

    @PostMapping("/orders")
    @ApiOperation("\${swagger.doc.operation.order.post-order.description}")
    @PreAuthorize("isAuthenticated() and ( hasRole('ROLE_ADMIN') or ( #dto.userId == principal.userId ) )")
    @ResponseStatus(HttpStatus.CREATED)
    fun postOrder(@RequestBody body: OrderRequest.PostBody) =
        orderService.createOrder(body.toDtoForCreate())

    @PatchMapping("/orders/{id}")
    @ApiOperation("\${swagger.doc.operation.order.patch-order.description}")
    fun patchOrder(@PathVariable id: UUID, @RequestBody body: OrderRequest.PatchBody) =
        orderService.updateOrder(id, body.toDtoForUpdate())

    @DeleteMapping("/orders/{id}")
    @PreAuthorize("isAuthenticated() and hasRole('ROLE_ADMIN')")
    @ApiOperation("\${swagger.doc.operation.order.delete-order.description}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteOrder(@PathVariable id: UUID) {
        val deleted = orderService.deleteOrder(id)
        if (!deleted) throw OrderNotFoundException()
    }
}