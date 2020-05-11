package micronaut.bookman.domain.book.exceptions

import micronaut.bookman.exceptions.ErrorCode

class IllegalBookStateException (
        override val message: String?,
        override val cause: Throwable?
) : BookDomainException(ErrorCode.ILLEGAL_BOOK_STATE, message, cause) {
    constructor(message: String?) : this(message, null)
    constructor(cause: Throwable?) : this(cause?.toString(), cause)
    constructor() : this(null, null)
}