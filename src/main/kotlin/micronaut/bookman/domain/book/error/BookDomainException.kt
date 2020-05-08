package micronaut.bookman.domain.book.error

open class BookDomainException(override val message: String?, override val cause: Throwable?) : Throwable(message, cause) {
    constructor(message: String?) : this(message, null)
    constructor(cause: Throwable?) : this(cause?.toString(), cause)
    constructor() : this(null, null)
}