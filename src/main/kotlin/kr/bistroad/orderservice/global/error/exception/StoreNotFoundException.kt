package kr.bistroad.orderservice.global.error.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Store not found")
class StoreNotFoundException : RuntimeException {
    constructor() : super()
    constructor(throwable: Throwable) : super(throwable)
}