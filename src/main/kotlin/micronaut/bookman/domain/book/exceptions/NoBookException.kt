package micronaut.bookman.domain.book.exceptions

import micronaut.bookman.domain.book.BookId
import micronaut.bookman.exceptions.ErrorCode

class NoBookException private constructor(
        override val message: String?,
        override val cause: Throwable?
) : BookDomainException(ErrorCode.BOOK_NOT_FOUND, message, cause) {
    constructor(id: BookId) : this("Book(id = ${id})", null)
}