package micronaut.bookman.domain.person.exceptions

import micronaut.bookman.exceptions.ErrorCode

class IllegalPersonStateException(
        override val message: String?,
        override val cause: Throwable?
) : PersonDomainException(ErrorCode.ILLEGAL_PERSON_STATE, message, cause) {
    constructor(message: String?) : this(message, null)
    constructor(cause: Throwable?) : this(cause?.toString(), cause)
    constructor() : this(null, null)
}