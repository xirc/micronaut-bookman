package micronaut.bookman.infra.exceptions

import micronaut.bookman.exceptions.ApplicationException

open class InfraException(override val message: String?, override val cause: Throwable?) : ApplicationException(message, cause) {
    constructor(message: String?) : this(message, null)
    constructor(cause: Throwable?) : this(cause?.toString(), cause)
    constructor() : this(null, null)
}