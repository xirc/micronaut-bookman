package micronaut.bookman.domain.book.exceptions

import micronaut.bookman.exceptions.ApplicationException
import micronaut.bookman.exceptions.ErrorCode

open class BookDomainException(
        override val code: ErrorCode,
        override val message: String?,
        override val cause: Throwable?
) : ApplicationException(code, message, cause) {
    constructor(code: ErrorCode, message: String?) : this(code, message, null)
    constructor(code: ErrorCode, cause: Throwable?) : this(code, cause?.toString(), cause)
    constructor(code: ErrorCode) : this(code, null, null)
}