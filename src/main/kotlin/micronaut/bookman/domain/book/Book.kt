package micronaut.bookman.domain.book

import micronaut.bookman.domain.book.error.IllegalBookStateException
import micronaut.bookman.domain.time.DateTimeFactory
import org.joda.time.DateTime
import java.util.*

class Book private constructor(val id: String, val createdDate: DateTime, private val timeFactory: DateTimeFactory) {
    var title: String = ""
        private set
    var updatedDate: DateTime = createdDate
        private set(value) {
            if (value.isBefore(createdDate)) throw IllegalBookStateException("UpdatedDate should be after CreatedDate.")
            field = value
        }
    var author: BookAuthor? = null
        private set

    fun updateTitle(title: String) {
        this.title = title
        this.updatedDate = timeFactory.now()
    }

    fun updateAuthor(author: BookAuthor) {
        this.author = author
        this.updatedDate = timeFactory.now()
    }

    class Factory(private val timeFactory: DateTimeFactory) {
        fun create() = Book(UUID.randomUUID().toString(), timeFactory.now(), timeFactory)
        fun createFromRepository(
                id: String,
                title: String,
                createdDate: DateTime,
                updatedDate: DateTime,
                author: BookAuthor?
        ) = Book(id, createdDate, timeFactory).apply {
            this.title = title
            this.updatedDate = updatedDate
            this.author = author
        }
    }
}