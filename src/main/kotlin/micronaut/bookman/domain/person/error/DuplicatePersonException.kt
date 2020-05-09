package micronaut.bookman.domain.person.error

class DuplicatePersonException private constructor(
        override val message: String?,
        override val cause: Throwable?
) : PersonDomainException(message, cause) {
    constructor(id: String) : this("Person(id = ${id})", null)
}