package micronaut.bookman.domain.book.error

import micronaut.bookman.error.ApplicationException

open class BookDomainException(override val message: String?, override val cause: Throwable?) : ApplicationException(message, cause) {
    constructor(message: String?) : this(message, null)
    constructor(cause: Throwable?) : this(cause?.toString(), cause)
    constructor() : this(null, null)
}