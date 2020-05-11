package micronaut.bookman.exceptions

import micronaut.bookman.controller.ErrorResponseBody
import micronaut.bookman.domain.book.exceptions.NoBookException

object BookErrorResponseSyntax {
    fun NoBookException.toResponseBody() = ErrorResponseBody(ErrorCode.BOOK_NOT_FOUND, this.message
            ?: "")
}