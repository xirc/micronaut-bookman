package micronaut.bookman.usecase

import micronaut.bookman.domain.book.Book
import micronaut.bookman.domain.book.BookRepository
import java.util.*

class LibrarianBookUseCase(
        private val repository: BookRepository
) {
    fun getBook(id: UUID): Book {
        return repository.get(id)
    }

    fun createBook(title: String): Book {
        val book = Book.create()
        book.updateTitle(title)
        return repository.save(book)
    }

    fun deleteBook(id: UUID) {
        repository.delete(id)
    }

    fun patchBook(id: UUID, title: String?): Book {
        val book = repository.get(id)
        if (title != null) {
            book.updateTitle(title)
        }
        return repository.update(book)
    }
}