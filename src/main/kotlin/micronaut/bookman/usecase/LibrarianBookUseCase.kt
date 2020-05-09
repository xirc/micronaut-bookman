package micronaut.bookman.usecase

import micronaut.bookman.domain.book.Book
import micronaut.bookman.domain.book.BookAuthor
import micronaut.bookman.domain.book.BookRepository
import micronaut.bookman.domain.person.PersonRepository
import javax.inject.Singleton

@Singleton
class LibrarianBookUseCase(
        private val factory: Book.Factory,
        private val repository: BookRepository,
        private val personRepository: PersonRepository
) {
    fun getBook(id: String): BookDto {
        val book = repository.get(id)
        val author = book.author?.let { personRepository.get(it.personId) }
        return BookDto.createFrom(book, author)
    }

    fun createBook(title: String): BookDto {
        val book = factory.create()
        book.updateTitle(title)
        val savedBook = repository.save(book)
        return BookDto.createFrom(savedBook, null)
    }

    fun deleteBook(id: String) {
        repository.delete(id)
    }

    fun patchBook(
            id: String,
            title: String? = null,
            authorId: String? = null
    ): BookDto {
        val book = repository.get(id)
        if (title != null) {
            book.updateTitle(title)
        }
        authorId?.let {
            book.updateAuthor(BookAuthor(authorId))
        }
        val updatedBook = repository.update(book)
        val author = book.author?.let { personRepository.get(it.personId) }
        return BookDto.createFrom(updatedBook, author)
    }
}