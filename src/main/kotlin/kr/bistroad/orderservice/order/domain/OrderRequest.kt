package kr.bistroad.orderservice.order.domain

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "order_requests")
class OrderRequest(
    @Id
    @GeneratedValue
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "orderId")
    var order: Order? = null,

    @Column(columnDefinition = "BINARY(16)")
    val itemId: UUID,

    var amount: Int
)