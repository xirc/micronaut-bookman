package micronaut.bookman.domain.person.exceptions

import micronaut.bookman.exceptions.ErrorCode

class DuplicatePersonException private constructor(
        override val message: String?,
        override val cause: Throwable?
) : PersonDomainException(ErrorCode.DUPLICATE_PERSON, message, cause) {
    constructor(id: String) : this("Person(id = ${id})", null)
}