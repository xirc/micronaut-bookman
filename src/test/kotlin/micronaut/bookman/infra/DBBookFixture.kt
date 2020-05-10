package micronaut.bookman.infra

import micronaut.bookman.domain.book.Book
import micronaut.bookman.infra.book.DBBookRepository
import java.util.*
import javax.inject.Singleton
import javax.sql.DataSource

@Singleton
class DBBookFixture (
        private val source: DataSource,
        private val factory: Book.Factory
) {
    private val repository = DBBookRepository(source, factory)

    fun create(): Book {
        val q = UUID.randomUUID().toString()
        val book = factory.create()
        book.updateTitle("t$q")
        repository.save(book)
        return book
    }
    fun createCollection(n: Int): List<Book> {
        // TODO Batch Insert する
        var books = mutableListOf<Book>()
        for (i in 0 until n) {
            val q = UUID.randomUUID().toString()
            val book = factory.create()
            book.updateTitle("t$q")
            val savedBook = repository.save(book)
            books.add(savedBook)
        }
        return books
    }
}