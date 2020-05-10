package micronaut.bookman.domain.person.error

class NoPersonException private constructor(
        override val message: String?,
        override val cause: Throwable?
) : PersonDomainException(message, cause) {
    constructor(any: List<String>): this("Person(any = ${any})")
    constructor(id: String) : this("Person(id = ${id})", null)
}