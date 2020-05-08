package micronaut.bookman.controller

import micronaut.bookman.domain.book.error.NoBookException

object BookErrorResponseSyntax {
    fun NoBookException.toResponseBody() = ErrorResponseBody(ErrorCode.BOOK_NOT_FOUND, this.message ?: "")
}