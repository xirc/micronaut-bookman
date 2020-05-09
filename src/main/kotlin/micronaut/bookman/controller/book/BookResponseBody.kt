package micronaut.bookman.controller.book

import micronaut.bookman.controller.person.PersonResponseBody
import micronaut.bookman.domain.book.Book
import micronaut.bookman.domain.person.Person
import org.joda.time.DateTime

data class BookResponseBody private constructor(
        val id: String,
        val title: String,
        val author: PersonResponseBody?,
        val createdDate: DateTime,
        val updatedDate: DateTime
) {
    companion object {
        fun createFrom(book: Book, author: Person?) = BookResponseBody(
                book.id,
                book.title,
                author?.let { PersonResponseBody.createFrom(it) },
                book.createdDate,
                book.updatedDate
        )
    }
}