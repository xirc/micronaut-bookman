package micronaut.bookman.domain.person.error

class NoPersonException private constructor(
        override val message: String?,
        override val cause: Throwable?
) : PersonDomainException(message, cause) {
    constructor(id: String) : this("Person(id = ${id})", null)
}