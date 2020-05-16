package micronaut.bookman.domain.person.exceptions

import micronaut.bookman.domain.person.PersonId
import micronaut.bookman.exceptions.ErrorCode

class NoPersonException private constructor(
        override val message: String?,
        override val cause: Throwable?
) : PersonDomainException(ErrorCode.PERSON_NOT_FOUND, message, cause) {
    constructor(any: List<PersonId>): this("Person(any = ${any})", null)
    constructor(id: PersonId) : this("Person(id = ${id})", null)
}