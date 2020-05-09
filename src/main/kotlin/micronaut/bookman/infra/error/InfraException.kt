package micronaut.bookman.infra.error

open class InfraException(override val message: String?, override val cause: Throwable?) : Throwable(message, cause) {
    constructor(message: String?) : this(message, null)
    constructor(cause: Throwable?) : this(cause?.toString(), cause)
    constructor() : this(null, null)
}