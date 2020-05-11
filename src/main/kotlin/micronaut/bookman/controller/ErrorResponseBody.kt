package micronaut.bookman.controller

import micronaut.bookman.exceptions.ErrorCode

data class ErrorResponseBody(val id: ErrorCode, val message: String)