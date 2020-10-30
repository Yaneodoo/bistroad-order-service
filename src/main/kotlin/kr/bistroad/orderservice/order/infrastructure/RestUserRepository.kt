package kr.bistroad.orderservice.order.infrastructure

import kr.bistroad.orderservice.order.domain.User
import kr.bistroad.orderservice.order.domain.UserRepository
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject
import java.util.*

@Component
class RestUserRepository(
    private val restTemplate: RestTemplate
) : UserRepository {
    override fun findByIdOrNull(id: UUID): User? =
        try {
            restTemplate.getForObject<User>("http://user-service:8080/users/$id")
        } catch (ex: HttpClientErrorException.NotFound) {
            null
        }
}