package micronaut.bookman.domain.book.error

class IllegalBookStateException (
        override val message: String?,
        override val cause: Throwable?
) : BookDomainException(message, cause) {
    constructor(message: String?) : this(message, null)
    constructor(cause: Throwable?) : this(cause?.toString(), cause)
    constructor() : this(null, null)
}