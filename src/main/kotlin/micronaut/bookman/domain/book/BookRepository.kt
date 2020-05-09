package micronaut.bookman.domain.book

import java.util.*

interface BookRepository {
    fun get(id: UUID): Book
    fun save(book: Book): Book
    fun update(book: Book): Book
    fun delete(id: UUID)
}