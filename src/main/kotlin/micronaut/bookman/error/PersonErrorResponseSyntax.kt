package micronaut.bookman.error

import micronaut.bookman.controller.ErrorResponseBody
import micronaut.bookman.domain.person.error.NoPersonException

object PersonErrorResponseSyntax {
    fun NoPersonException.toResponseBody() = ErrorResponseBody(ErrorCode.PERSON_NOT_FOUND, this.message
            ?: "")
}