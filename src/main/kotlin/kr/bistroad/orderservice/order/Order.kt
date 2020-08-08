package kr.bistroad.orderservice.order

import org.hibernate.annotations.GenericGenerator
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "orders")
class Order(
        @Id
        @GeneratedValue(generator = "uuid2")
        @GenericGenerator(name = "uuid2", strategy = "uuid2")
        @Column(columnDefinition = "BINARY(16)")
        val id: UUID? = null,

        @Column(columnDefinition = "BINARY(16)")
        val storeId: UUID,

        @Column(columnDefinition = "BINARY(16)")
        val userId: UUID,

        @OneToMany(mappedBy = "order", fetch = FetchType.EAGER, cascade = [CascadeType.ALL], orphanRemoval = true)
        @Column(name = "requests")
        val _requests: MutableList<OrderRequest> = mutableListOf(),

        val date: Date,
        val tableNum: Int,
        var progress: Progress
) {
    val requests: List<OrderRequest>
        get() = _requests

    fun addRequest(request: OrderRequest) {
        request.order = this
        _requests += request
    }

    enum class Progress {
        REQUESTED, ACCEPTED
    }
}