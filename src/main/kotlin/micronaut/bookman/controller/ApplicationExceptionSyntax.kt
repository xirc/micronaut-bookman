package micronaut.bookman.controller

import micronaut.bookman.exceptions.ApplicationException

object ApplicationExceptionSyntax {
    fun ApplicationException.toResponseBody() =
        ErrorResponseBody(this.code, this.message
                ?: "")
}