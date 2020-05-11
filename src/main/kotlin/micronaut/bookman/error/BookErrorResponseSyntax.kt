package micronaut.bookman.error

import micronaut.bookman.controller.ErrorResponseBody
import micronaut.bookman.domain.book.error.NoBookException

object BookErrorResponseSyntax {
    fun NoBookException.toResponseBody() = ErrorResponseBody(ErrorCode.BOOK_NOT_FOUND, this.message
            ?: "")
}