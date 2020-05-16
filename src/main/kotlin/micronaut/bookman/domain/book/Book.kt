package micronaut.bookman.domain.book

import micronaut.bookman.domain.book.exceptions.IllegalBookStateException
import micronaut.bookman.domain.time.DateTimeFactory
import org.joda.time.DateTime
import javax.inject.Singleton

class Book private constructor(
        val id: BookId,
        val createdDate: DateTime,
        private val timeFactory: DateTimeFactory
) {
    var title: String = DefaultTitle
        private set
    var updatedDate: DateTime = createdDate
        private set(value) {
            if (value.isBefore(createdDate)) throw IllegalBookStateException("UpdatedDate should be after CreatedDate.")
            field = value
        }
    var authors: List<BookAuthor> = mutableListOf()
        private set

    fun updateTitle(title: String) {
        this.title = title
        this.updatedDate = timeFactory.now()
    }

    fun updateAuthors(authors: List<BookAuthor>) {
        this.authors = authors
        this.updatedDate = timeFactory.now()
    }

    @Singleton
    class Factory(private val timeFactory: DateTimeFactory) {
        fun create() = Book(BookId(), timeFactory.now(), timeFactory)
        fun createFromRepository(
                id: BookId,
                title: String,
                createdDate: DateTime,
                updatedDate: DateTime,
                authors: List<BookAuthor>
        ) = Book(id, createdDate, timeFactory).apply {
            this.title = title
            this.updatedDate = updatedDate
            this.authors = authors
        }
    }

    companion object {
        const val DefaultTitle = ""
    }
}