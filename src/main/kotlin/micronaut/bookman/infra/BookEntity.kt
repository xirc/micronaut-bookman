package micronaut.bookman.infra

import io.micronaut.core.annotation.Introspected
import io.micronaut.data.annotation.DateCreated
import io.micronaut.data.annotation.DateUpdated
import io.micronaut.data.annotation.MappedEntity
import micronaut.bookman.domain.book.Book
import org.joda.time.DateTime
import java.time.Instant
import java.util.*
import javax.persistence.Id

@Introspected
@MappedEntity("book")
class BookEntity {
    @Id
    var id: UUID = UUID.randomUUID()
    @DateCreated
    var createdDate: Instant = Instant.now()
    @DateUpdated
    var updatedDate: Instant = Instant.now()
    var title: String = ""

    fun to(): Book = Book.createFromRepository(
            id,
            DateTime(createdDate.toEpochMilli()),
            DateTime(updatedDate.toEpochMilli()),
            title
    )

    companion object {
        fun from(book: Book) = BookEntity().apply {
            id = book.id
            createdDate = Instant.ofEpochMilli(book.createdDate.millis)
            updatedDate = Instant.ofEpochMilli(book.updatedDate.millis)
            title = book.title
        }
    }
}