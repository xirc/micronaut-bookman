package micronaut.bookman.usecase

import micronaut.bookman.domain.book.Book
import micronaut.bookman.domain.book.BookAuthor
import micronaut.bookman.domain.book.BookRepository
import micronaut.bookman.domain.person.Person
import micronaut.bookman.domain.person.PersonRepository
import javax.inject.Singleton

@Singleton
class LibrarianBookUseCase(
        private val factory: Book.Factory,
        private val repository: BookRepository,
        private val personRepository: PersonRepository
) {
    data class BookDTO(val book: Book, val author: Person?)

    fun getBook(id: String): BookDTO {
        val book = repository.get(id)
        val person = book.author?.let { personRepository.get(it.personId) }
        return BookDTO(book, person)
    }

    fun createBook(title: String): BookDTO {
        val book = factory.create()
        book.updateTitle(title)
        val savedBook = repository.save(book)
        return BookDTO(savedBook, null)
    }

    fun deleteBook(id: String) {
        repository.delete(id)
    }

    fun patchBook(
            id: String,
            title: String? = null,
            authorId: String? = null
    ): BookDTO {
        val book = repository.get(id)
        if (title != null) {
            book.updateTitle(title)
        }
        val author = authorId?.let {
            book.updateAuthor(BookAuthor(authorId))
            personRepository.get(authorId)
        }
        val updatedBook = repository.update(book)
        return BookDTO(updatedBook, author)
    }
}