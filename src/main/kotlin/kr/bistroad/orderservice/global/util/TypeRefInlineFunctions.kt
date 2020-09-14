package kr.bistroad.orderservice.global.util

import org.springframework.core.ParameterizedTypeReference

inline fun <reified T : Any> typeRef(): ParameterizedTypeReference<T> = object : ParameterizedTypeReference<T>() {}