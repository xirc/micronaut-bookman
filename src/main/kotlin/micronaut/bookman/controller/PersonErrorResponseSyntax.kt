package micronaut.bookman.controller

import micronaut.bookman.domain.person.error.NoPersonException

object PersonErrorResponseSyntax {
    fun NoPersonException.toResponseBody() = ErrorResponseBody(ErrorCode.PERSON_NOT_FOUND, this.message ?: "")
}