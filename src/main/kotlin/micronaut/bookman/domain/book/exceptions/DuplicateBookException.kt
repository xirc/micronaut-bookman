package micronaut.bookman.domain.book.exceptions

import micronaut.bookman.exceptions.ErrorCode

class DuplicateBookException private constructor(
        override val message: String?,
        override val cause: Throwable?
) : BookDomainException(ErrorCode.DUPLICATE_BOOK, message, cause) {
    constructor(id: String) : this("Book(id = ${id})", null)
}