package micronaut.bookman.domain.book.error

class DuplicateBookException private constructor(
        override val message: String?,
        override val cause: Throwable?
) : BookDomainException(message, cause) {
    constructor(id: String) : this("Book(id = ${id})", null)
}