package micronaut.bookman.domain.book

import org.joda.time.DateTime
import java.util.*

class Book private constructor(
        val id: UUID,
        val createdDate: DateTime,
        val updatedDate: DateTime
) {
    var title: String = ""
        private set

    fun updateTitle(title: String) {
        this.title = title
    }

    companion object {
        fun create() = Book(
                UUID.randomUUID(),
                DateTime.now(),
                DateTime.now()
        )
        fun createFromRepository(
                id: UUID,
                createdDate: DateTime,
                updatedDate: DateTime,
                title: String
        ) = Book(
                id,
                createdDate,
                updatedDate
        ).apply {
            this.title = title
        }
    }
}