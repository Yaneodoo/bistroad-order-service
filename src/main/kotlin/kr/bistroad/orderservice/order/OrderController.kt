package kr.bistroad.orderservice.order

import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiOperation
import kr.bistroad.orderservice.exception.OrderNotFoundException
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@Api(tags = ["/stores/**/orders"])
class OrderController(
    private val orderService: OrderService
) {
    @GetMapping("/stores/{storeId}/orders/{id}")
    @ApiOperation("\${swagger.doc.operation.order.get-order.description}")
    fun getOrder(@PathVariable storeId: UUID, @PathVariable id: UUID) =
        orderService.readOrder(storeId, id) ?: throw OrderNotFoundException()

    @GetMapping("/stores/{storeId}/orders")
    @ApiOperation("\${swagger.doc.operation.order.get-orders.description}")
    fun getOrders(@PathVariable storeId: UUID, dto: OrderDto.SearchReq, pageable: Pageable) =
        orderService.searchOrders(storeId, dto, pageable)

    @PostMapping("/stores/{storeId}/orders")
    @ApiOperation("\${swagger.doc.operation.order.post-order.description}")
    @ApiImplicitParam(
        name = "Authorization", value = "Access Token", required = true, paramType = "header",
        allowEmptyValue = false, dataTypeClass = String::class, example = "Bearer access_token"
    )
    @PreAuthorize("isAuthenticated() and (( #dto.userId == principal.userId ) or hasRole('ROLE_ADMIN'))")
    @ResponseStatus(HttpStatus.CREATED)
    fun postOrder(@PathVariable storeId: UUID, @RequestBody dto: OrderDto.CreateReq) =
        orderService.createOrder(storeId, dto)

    @PatchMapping("/stores/{storeId}/orders/{id}")
    @ApiOperation("\${swagger.doc.operation.order.patch-order.description}")
    @ApiImplicitParam(
        name = "Authorization", value = "Access Token", required = true, paramType = "header",
        allowEmptyValue = false, dataTypeClass = String::class, example = "Bearer access_token"
    )
    @PreAuthorize("isAuthenticated() and (( hasPermission(#storeId, 'Order', 'write') ) or hasRole('ROLE_ADMIN'))")
    fun patchOrder(@PathVariable storeId: UUID, @PathVariable id: UUID, @RequestBody dto: OrderDto.PatchReq) =
        orderService.patchOrder(storeId, id, dto)

    @DeleteMapping("/stores/{storeId}/orders/{id}")
    @PreAuthorize("isAuthenticated() and hasRole('ROLE_ADMIN')")
    @ApiImplicitParam(
        name = "Authorization", value = "Access Token", required = true, paramType = "header",
        allowEmptyValue = false, dataTypeClass = String::class, example = "Bearer access_token"
    )
    @ApiOperation("\${swagger.doc.operation.order.delete-order.description}")
    fun deleteOrder(@PathVariable storeId: UUID, @PathVariable id: UUID): ResponseEntity<Void> =
        if (orderService.deleteOrder(storeId, id))
            ResponseEntity.noContent().build()
        else
            ResponseEntity.notFound().build()
}