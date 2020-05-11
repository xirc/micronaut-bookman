package micronaut.bookman.exceptions

import micronaut.bookman.controller.ErrorResponseBody
import micronaut.bookman.domain.person.exceptions.NoPersonException

object PersonErrorResponseSyntax {
    fun NoPersonException.toResponseBody() = ErrorResponseBody(ErrorCode.PERSON_NOT_FOUND, this.message
            ?: "")
}