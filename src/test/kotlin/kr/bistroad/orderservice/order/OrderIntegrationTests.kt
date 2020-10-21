package kr.bistroad.orderservice.order

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.ninjasquad.springmockk.MockkBean
import io.kotest.matchers.collections.shouldBeSingleton
import io.kotest.matchers.shouldBe
import io.mockk.every
import kr.bistroad.orderservice.order.domain.*
import kr.bistroad.orderservice.order.infrastructure.OrderRepository
import kr.bistroad.orderservice.order.infrastructure.RestStoreRepository
import kr.bistroad.orderservice.order.presentation.OrderRequest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject
import org.springframework.web.client.postForObject
import java.time.OffsetDateTime
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
internal class OrderIntegrationTests {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var orderRepository: OrderRepository

    @MockkBean
    private lateinit var restTemplate: RestTemplate

    private val objectMapper: ObjectMapper = ObjectMapper().registerKotlinModule()

    @AfterEach
    fun clear() = orderRepository.deleteAll()

    @Test
    fun `Gets an order`() {
        val order = randomOrder()
        orderRepository.save(order)

        mockMvc.perform(
            get("/orders/${order.id}")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("\$.id").value(order.id.toString()))
            .andExpect(jsonPath("\$.store.id").value(order.store.id.toString()))
            .andExpect(jsonPath("\$.store.name").value(order.store.name))
            .andExpect(jsonPath("\$.userId").value(order.customer.id.toString()))
            .andExpect(jsonPath("\$.progress").value(order.progress.toString()))
    }

    @Test
    fun `Searches orders`() {
        val now = OffsetDateTime.now()
        val orderA = randomOrder(timestamp = now.minusDays(1))
        val orderB = randomOrder(timestamp = now)
        val orderC = randomOrder(timestamp = now.minusMinutes(1))

        orderRepository.save(orderA)
        orderRepository.save(orderB)
        orderRepository.save(orderC)

        mockMvc.perform(
            get("/orders?sort=timestamp,desc")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("\$.[0].id").value(orderB.id.toString()))
            .andExpect(jsonPath("\$.[1].id").value(orderC.id.toString()))
            .andExpect(jsonPath("\$.[2].id").value(orderA.id.toString()))
    }

    @Test
    fun `Posts an order`() {
        val store = Store(
            id = UUID.randomUUID(),
            owner = StoreOwner(UUID.randomUUID()),
            name = "Store"
        )
        val item1 = OrderedItem(
            id = UUID.randomUUID(),
            name = "example",
            price = 100.0
        )
        val item2 = item1.copy(
            id = UUID.randomUUID()
        )

        every {
            restTemplate.getForObject<RestStoreRepository.RestStore>("http://store-service:8080/stores/${store.id}")
        } returns RestStoreRepository.RestStore(store.id, store.owner.id, store.name)
        every {
            restTemplate.getForObject<OrderedItem>("http://store-service:8080/stores/${store.id}/items/${item1.id}")
        } returns item1
        every {
            restTemplate.postForObject<OrderedItem>(
                "http://store-service:8080/stores/${store.id}/items/${item1.id}/add-order-count",
                any()
            )
        } returns item1
        every {
            restTemplate.getForObject<OrderedItem>("http://store-service:8080/stores/${store.id}/items/${item2.id}")
        } returns item2
        every {
            restTemplate.postForObject<OrderedItem>(
                "http://store-service:8080/stores/${store.id}/items/${item2.id}/add-order-count",
                any()
            )
        } returns item2

        val body = OrderRequest.PostBody(
            userId = UUID.randomUUID(),
            storeId = store.id,
            orderLines = listOf(
                OrderRequest.PostBody.OrderLine(itemId = item1.id, amount = 1),
                OrderRequest.PostBody.OrderLine(itemId = item2.id, amount = 2)
            ),
            timestamp = "2020-09-22T11:31:19Z",
            tableNum = 0,
            progress = OrderProgress.REQUESTED
        )

        mockMvc.perform(
            post("/orders")
                .header("Authorization-Role", "ROLE_ADMIN")
                .content(objectMapper.writeValueAsString(body))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("\$.id").exists())
            .andExpect(jsonPath("\$.userId").value(body.userId.toString()))
            .andExpect(jsonPath("\$.orderLines.[0].item.id").value(body.orderLines[0].itemId.toString()))
            .andExpect(jsonPath("\$.orderLines.[1].item.id").value(body.orderLines[1].itemId.toString()))
            .andExpect(jsonPath("\$.timestamp").value(body.timestamp!!))
            .andExpect(jsonPath("\$.progress").value("REQUESTED"))
    }

    @Test
    fun `Patches an order`() {
        val order = randomOrder(
            timestamp = OffsetDateTime.now(),
            progress = OrderProgress.REQUESTED
        )
        orderRepository.save(order)

        val body = OrderRequest.PatchBody(
            progress = OrderProgress.ACCEPTED
        )

        mockMvc.perform(
            patch("/orders/${order.id}")
                .header("Authorization-Role", "ROLE_ADMIN")
                .content(objectMapper.writeValueAsString(body))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("\$.id").value(order.id.toString()))
            .andExpect(jsonPath("\$.progress").value("ACCEPTED"))
            .andExpect(jsonPath("\$.tableNum").value(order.tableNum))
    }

    @Test
    fun `Deletes an order`() {
        val orderA = randomOrder()
        val orderB = randomOrder()

        orderRepository.save(orderA)
        orderRepository.save(orderB)

        every {
            restTemplate.postForObject<OrderedItem>(
                "http://store-service:8080/stores/${orderA.store.id}/items/${orderA.orderLines[0].item.id}" +
                        "/subtract-order-count",
                any()
            )
        } returns orderA.orderLines[0].item
        every {
            restTemplate.postForObject<OrderedItem>(
                "http://store-service:8080/stores/${orderA.store.id}/items/${orderA.orderLines[1].item.id}" +
                        "/subtract-order-count",
                any()
            )
        } returns orderA.orderLines[1].item

        mockMvc.perform(
            delete("/orders/${orderA.id}")
                .header("Authorization-Role", "ROLE_ADMIN")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)
            .andExpect(content().string(""))

        val orders = orderRepository.findAll()
        orders.shouldBeSingleton()
        orders.first().shouldBe(orderB)
    }

    private fun randomOrder(
        timestamp: OffsetDateTime = OffsetDateTime.now(),
        progress: OrderProgress = OrderProgress.REQUESTED
    ) = PlacedOrder(
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
        timestamp = timestamp,
        tableNum = 0,
        progress = progress
    )
}