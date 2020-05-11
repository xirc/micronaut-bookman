package micronaut.bookman.domain.person.exceptions

import micronaut.bookman.exceptions.ErrorCode

class NoPersonException private constructor(
        override val message: String?,
        override val cause: Throwable?
) : PersonDomainException(ErrorCode.PERSON_NOT_FOUND, message, cause) {
    constructor(any: List<String>): this("Person(any = ${any})")
    constructor(id: String) : this("Person(id = ${id})", null)
}