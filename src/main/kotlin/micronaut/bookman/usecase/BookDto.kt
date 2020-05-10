package micronaut.bookman.usecase

import com.fasterxml.jackson.annotation.JsonInclude
import micronaut.bookman.domain.book.Book
import micronaut.bookman.domain.person.Person
import org.joda.time.DateTime

data class BookDto private constructor(
        val id: String,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        val title: String,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        val authors: List<PersonDto>,
        val createdDate: DateTime,
        val updatedDate: DateTime
) {
    companion object {
        fun createFrom(book: Book, authors: List<Person>) = BookDto(
                book.id,
                book.title,
                authors.map { PersonDto.createFrom(it) },
                book.createdDate,
                book.updatedDate
        )
    }
}