package micronaut.bookman.usecase

import micronaut.bookman.domain.book.Book
import micronaut.bookman.domain.book.BookRepository

class LibrarianBookUseCase(
        private val factory: Book.Factory,
        private val repository: BookRepository
) {
    fun getBook(id: String): Book {
        return repository.get(id)
    }

    fun createBook(title: String): Book {
        val book = factory.create()
        book.updateTitle(title)
        repository.post(book)
        return book
    }

    fun deleteBook(id: String) {
        repository.delete(id)
    }

    fun patchBook(id: String, title: String?): Book {
        val book = repository.get(id)
        if (title != null) {
            book.updateTitle(title)
        }
        repository.put(book)
        return book
    }
}