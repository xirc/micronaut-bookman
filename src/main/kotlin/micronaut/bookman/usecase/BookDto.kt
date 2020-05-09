package micronaut.bookman.usecase

import micronaut.bookman.domain.book.Book
import micronaut.bookman.domain.person.Person
import org.joda.time.DateTime

data class BookDto private constructor(
        val id: String,
        val title: String,
        val author: PersonDto?,
        val createdDate: DateTime,
        val updatedDate: DateTime
) {
    companion object {
        fun createFrom(book: Book, author: Person?) = BookDto(
                book.id,
                book.title,
                author?.let { PersonDto.createFrom(it) },
                book.createdDate,
                book.updatedDate
        )
    }
}