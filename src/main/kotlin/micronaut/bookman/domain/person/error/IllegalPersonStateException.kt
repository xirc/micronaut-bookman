package micronaut.bookman.domain.person.error

class IllegalPersonStateException(
        override val message: String?,
        override val cause: Throwable?
) : PersonDomainException(message, cause) {
    constructor(message: String?) : this(message, null)
    constructor(cause: Throwable?) : this(cause?.toString(), cause)
    constructor() : this(null, null)
}