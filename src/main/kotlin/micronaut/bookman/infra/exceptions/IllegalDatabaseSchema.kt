package micronaut.bookman.infra.exceptions

class IllegalDatabaseSchema(override val message: String?, override val cause: Throwable?) : InfraException(message, cause) {
    constructor(message: String?) : this(message, null)
    constructor(cause: Throwable?) : this(cause?.toString(), cause)
    constructor() : this(null, null)
}