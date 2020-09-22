package kr.bistroad.orderservice.order

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.ninjasquad.springmockk.MockkBean
import io.kotest.matchers.collections.shouldBeSingleton
import io.kotest.matchers.shouldBe
import io.mockk.every
import kr.bistroad.orderservice.order.application.OrderDto
import kr.bistroad.orderservice.order.domain.RequestedOrder
import kr.bistroad.orderservice.order.infrastructure.OrderRepository
import kr.bistroad.orderservice.order.infrastructure.Store
import kr.bistroad.orderservice.order.presentation.OrderRequest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.http.ResponseEntity
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
internal class OrderIntegrationTests {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var orderRepository: OrderRepository

    @MockkBean
    private lateinit var restTemplate: RestTemplate

    private val objectMapper: ObjectMapper = ObjectMapper().registerKotlinModule()

    @Test
    fun `Gets an order`() {
        val order = RequestedOrder(
            storeId = UUID.randomUUID(),
            userId = UUID.randomUUID(),
            date = OffsetDateTime.now().toDate(),
            tableNum = 0,
            progress = RequestedOrder.Progress.REQUESTED
        )

        orderRepository.save(order)

        mockMvc.perform(
            get("/orders/${order.id}")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("\$.id").value(order.id.toString()))
            .andExpect(jsonPath("\$.storeId").value(order.storeId.toString()))
            .andExpect(jsonPath("\$.userId").value(order.userId.toString()))
            .andExpect(jsonPath("\$.progress").value(order.progress.toString()))
    }

    @Test
    fun `Searches orders`() {
        val now = OffsetDateTime.now()
        val orderA = RequestedOrder(
            storeId = UUID.randomUUID(),
            userId = UUID.randomUUID(),
            date = now.minusDays(1).toDate(),
            tableNum = 0,
            progress = RequestedOrder.Progress.REQUESTED
        )
        val orderB = RequestedOrder(
            storeId = UUID.randomUUID(),
            userId = UUID.randomUUID(),
            date = now.toDate(),
            tableNum = 0,
            progress = RequestedOrder.Progress.REQUESTED
        )
        val orderC = RequestedOrder(
            storeId = UUID.randomUUID(),
            userId = UUID.randomUUID(),
            date = now.minusMinutes(1).toDate(),
            tableNum = 0,
            progress = RequestedOrder.Progress.ACCEPTED
        )

        orderRepository.save(orderA)
        orderRepository.save(orderB)
        orderRepository.save(orderC)

        mockMvc.perform(
            get("/orders?sort=date,desc")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("\$.[0].id").value(orderB.id.toString()))
            .andExpect(jsonPath("\$.[1].id").value(orderC.id.toString()))
            .andExpect(jsonPath("\$.[2].id").value(orderA.id.toString()))
    }

    @Test
    fun `Posts an order`() {
        val dateString = "2020-09-22T11:31:19.000+00:00"
        val body = OrderRequest.PostBody(
            userId = UUID.randomUUID(),
            storeId = UUID.randomUUID(),
            requests = listOf(
                OrderRequest.PostBody.Request(itemId = UUID.randomUUID(), amount = 1),
                OrderRequest.PostBody.Request(itemId = UUID.randomUUID(), amount = 2)
            ),
            date = OffsetDateTime.parse(dateString, DateTimeFormatter.ISO_DATE_TIME).toDate(),
            tableNum = 0,
            progress = RequestedOrder.Progress.REQUESTED
        )
        val item1 = OrderDto.ForResult.StoreItem(
            id = body.requests[0].itemId,
            name = "example", description = "example", price = 100.0,
            photoUri = null, stars = 4.5
        )
        val item2 = item1.copy(id = body.requests[1].itemId)

        every {
            restTemplate.getForObject(
                "http://store-service:8080/stores/${body.storeId}/items/${item1.id}",
                OrderDto.ForResult.StoreItem::class.java
            )
        } returns item1
        every {
            restTemplate.getForObject(
                "http://store-service:8080/stores/${body.storeId}/items/${item2.id}",
                OrderDto.ForResult.StoreItem::class.java
            )
        } returns item2
        every {
            restTemplate.exchange(any<RequestEntity<List<Any>>>(), any<ParameterizedTypeReference<List<Any>>>())
        } returns ResponseEntity(listOf(Any()), HttpStatus.OK)

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
            .andExpect(jsonPath("\$.requests.[0].item.id").value(body.requests[0].itemId.toString()))
            .andExpect(jsonPath("\$.requests.[1].item.id").value(body.requests[1].itemId.toString()))
            .andExpect(jsonPath("\$.date").value(dateString))
            .andExpect(jsonPath("\$.progress").value("REQUESTED"))
    }

    @Test
    fun `Patches an order`() {
        val order = RequestedOrder(
            storeId = UUID.randomUUID(),
            userId = UUID.randomUUID(),
            date = OffsetDateTime.now().toDate(),
            tableNum = 0,
            progress = RequestedOrder.Progress.REQUESTED
        )
        val body = OrderRequest.PatchBody(
            progress = RequestedOrder.Progress.ACCEPTED
        )

        orderRepository.save(order)

        val store = Store(ownerId = order.storeId)
        every {
            restTemplate.getForObject<Store>(
                "http://store-service:8080/stores/${order.storeId}"
            )
        } returns store

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

        orderRepository.save(order)
    }

    @Test
    fun `Deletes an order`() {
        val orderA = RequestedOrder(
            storeId = UUID.randomUUID(),
            userId = UUID.randomUUID(),
            date = OffsetDateTime.now().toDate(),
            tableNum = 0,
            progress = RequestedOrder.Progress.REQUESTED
        )
        val orderB = RequestedOrder(
            storeId = UUID.randomUUID(),
            userId = UUID.randomUUID(),
            date = OffsetDateTime.now().toDate(),
            tableNum = 0,
            progress = RequestedOrder.Progress.REQUESTED
        )

        orderRepository.save(orderA)
        orderRepository.save(orderB)

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

    private fun OffsetDateTime.toDate() = Date.from(this.toInstant())
}