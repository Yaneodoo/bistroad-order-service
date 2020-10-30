package kr.bistroad.orderservice.order.domain

import java.util.*

data class User(
    val id: UUID,
    var username: String,
    var fullName: String,
    var phone: String,
    var role: String,
    var photo: Photo? = null
)