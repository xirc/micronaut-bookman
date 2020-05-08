package micronaut.bookman.controller

data class ErrorResponseBody(val id: ErrorCode, val message: String)