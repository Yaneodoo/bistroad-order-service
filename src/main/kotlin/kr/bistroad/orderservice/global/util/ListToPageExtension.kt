package kr.bistroad.orderservice.global.util

import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

fun <T> List<T>.toPage(pageable: Pageable) = PageImpl<T>(this, pageable, this.size.toLong())