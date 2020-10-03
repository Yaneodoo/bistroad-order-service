package kr.bistroad.orderservice.order.infrastructure

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kr.bistroad.orderservice.order.domain.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import java.time.OffsetDateTime
import java.util.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
internal class OrderRepositoryTest {
    @Autowired
    private lateinit var orderRepository: OrderRepository

    @AfterEach
    fun clear() = orderRepository.deleteAll()

    @Test
    fun `Saves an order`() {
        val order = randomOrder()
        orderRepository.save(order)

        val foundOrder = orderRepository.findByIdOrNull(order.id)

        foundOrder.shouldNotBeNull()
        foundOrder.shouldBe(order)
    }

    @Test
    fun `Deletes a user`() {
        val order = randomOrder()
        orderRepository.save(order)

        val orderId = order.id
        val numDeleted = orderRepository.removeById(orderId)

        numDeleted.shouldBe(1)
        orderRepository.findByIdOrNull(orderId).shouldBeNull()
        orderRepository.findAll().shouldBeEmpty()
    }

    private fun randomOrder() = PlacedOrder(
        store = Store(
            id = UUID.randomUUID(),
            owner = StoreOwner(UUID.randomUUID()),
            name = "Store"
        ),
        customer = Customer(
            id = UUID.randomUUID()
        ),
        orderLines = mutableListOf(
            OrderLine(
                item = OrderedItem(
                    id = UUID.randomUUID(),
                    name = "a",
                    price = 1000.0
                ),
                amount = 1
            ),
            OrderLine(
                item = OrderedItem(
                    id = UUID.randomUUID(),
                    name = "b",
                    price = 0.001
                ),
                amount = 2
            )
        ),
        timestamp = OffsetDateTime.now(),
        tableNum = 0,
        progress = OrderProgress.REQUESTED
    )
}