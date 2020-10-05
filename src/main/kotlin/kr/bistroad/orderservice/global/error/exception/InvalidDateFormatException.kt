package kr.bistroad.orderservice.global.error.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Invalid date format")
class InvalidDateFormatException : RuntimeException {
    constructor() : super()
    constructor(throwable: Throwable) : super(throwable)
}