package kr.bistroad.orderservice.order.presentation

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import kr.bistroad.orderservice.global.error.exception.OrderNotFoundException
import kr.bistroad.orderservice.order.application.OrderDto
import kr.bistroad.orderservice.order.application.OrderService
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
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
    fun getOrders(dto: OrderDto.SearchReq, pageable: Pageable) =
        orderService.searchOrders(dto, pageable)

    @PostMapping("/orders")
    @ApiOperation("\${swagger.doc.operation.order.post-order.description}")
    @PreAuthorize("isAuthenticated() and (( #dto.userId == principal.userId ) or hasRole('ROLE_ADMIN'))")
    @ResponseStatus(HttpStatus.CREATED)
    fun postOrder(@RequestBody dto: OrderDto.CreateReq) =
        orderService.createOrder(dto)

    @PatchMapping("/orders/{id}")
    @ApiOperation("\${swagger.doc.operation.order.patch-order.description}")
    fun patchOrder(@PathVariable id: UUID, @RequestBody dto: OrderDto.PatchReq) =
        orderService.patchOrder(id, dto)

    @DeleteMapping("/orders/{id}")
    @PreAuthorize("isAuthenticated() and hasRole('ROLE_ADMIN')")
    @ApiOperation("\${swagger.doc.operation.order.delete-order.description}")
    fun deleteOrder(@PathVariable id: UUID): ResponseEntity<Void> =
        if (orderService.deleteOrder(id))
            ResponseEntity.noContent().build()
        else
            ResponseEntity.notFound().build()
}