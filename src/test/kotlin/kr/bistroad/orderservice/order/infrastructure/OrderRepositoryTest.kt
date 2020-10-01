package kr.bistroad.orderservice.order.infrastructure

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kr.bistroad.orderservice.order.domain.OrderRequest
import kr.bistroad.orderservice.order.domain.RequestedOrder
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.data.repository.findByIdOrNull
import java.time.OffsetDateTime
import java.util.*

@DataMongoTest
internal class OrderRepositoryTest {
    @Autowired
    private lateinit var orderRepository: OrderRepository

    @AfterEach
    fun clear() = orderRepository.deleteAll()

    @Test
    fun `Saves an order`() {
        val order = RequestedOrder(
            storeId = UUID.randomUUID(),
            userId = UUID.randomUUID(),
            requests = mutableListOf(
                OrderRequest(itemId = UUID.randomUUID(), amount = 1),
                OrderRequest(itemId = UUID.randomUUID(), amount = 2)
            ),
            date = OffsetDateTime.now().toDate(),
            tableNum = 0,
            progress = RequestedOrder.Progress.REQUESTED
        )
        orderRepository.save(order)

        val foundOrder = orderRepository.findByIdOrNull(order.id)

        foundOrder.shouldNotBeNull()
        foundOrder.shouldBe(order)
    }

    @Test
    fun `Deletes a user`() {
        val order = RequestedOrder(
            storeId = UUID.randomUUID(),
            userId = UUID.randomUUID(),
            requests = mutableListOf(
                OrderRequest(itemId = UUID.randomUUID(), amount = 1),
                OrderRequest(itemId = UUID.randomUUID(), amount = 2)
            ),
            date = OffsetDateTime.now().toDate(),
            tableNum = 0,
            progress = RequestedOrder.Progress.REQUESTED
        )
        orderRepository.save(order)

        val orderId = order.id
        val numDeleted = orderRepository.removeById(orderId)

        numDeleted.shouldBe(1)
        orderRepository.findByIdOrNull(orderId).shouldBeNull()
        orderRepository.findAll().shouldBeEmpty()
    }

    private fun OffsetDateTime.toDate() = Date.from(this.toInstant())
}