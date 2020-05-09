package micronaut.bookman.controller.book

import micronaut.bookman.domain.book.Book
import org.joda.time.DateTime

data class BookResponseBody private constructor(
        val id: String,
        val title: String,
        val createdDate: DateTime,
        val updatedDate: DateTime
) {
    companion object {
        fun createFrom(book: Book) = BookResponseBody(
                book.id,
                book.title,
                book.createdDate,
                book.updatedDate
        )
    }
}