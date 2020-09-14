package kr.bistroad.orderservice.global.config.security

import kr.bistroad.orderservice.order.infrastructure.Store
import org.springframework.security.access.PermissionEvaluator
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject
import java.io.Serializable

@Component
class OrderPermissionEvaluator : PermissionEvaluator {
    private val restTemplate: RestTemplate = RestTemplate()

    override fun hasPermission(authentication: Authentication?, targetDomainObject: Any?, permission: Any?) =
        throw UnsupportedOperationException()

    override fun hasPermission(
        authentication: Authentication?,
        targetId: Serializable?,
        targetType: String?,
        permission: Any?
    ): Boolean {
        if (authentication != null && targetType == "Order" && permission is String) {
            val userId = (authentication.principal as UserPrincipal).userId
            val store = try {
                restTemplate.getForObject<Store>("http://store-service:8080/stores/${targetId}")
            } catch (ex: HttpClientErrorException.NotFound) {
                null
            }

            when (permission) {
                "read" -> return true
                "write" -> return (store != null && store.ownerId == userId)
            }
        }
        return false
    }
}