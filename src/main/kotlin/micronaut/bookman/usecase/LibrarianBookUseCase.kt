package micronaut.bookman.usecase

import micronaut.bookman.domain.book.Book
import micronaut.bookman.domain.book.BookAuthor
import micronaut.bookman.domain.book.BookRepository
import micronaut.bookman.domain.person.PersonRepository
import java.lang.IllegalArgumentException
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

    fun createBook(title: String? = null): BookDto {
        val book = factory.create()
        book.updateTitle(title ?: "")
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

    fun listBook(
            page: Int
    ): BookCollectionDto {
        if (page < 0) throw IllegalArgumentException("page should be positive or zero.")
        val books = repository.getPage(page.toLong())
        val pageCount = repository.countPage(page.toLong())
        val persons = personRepository.getAll(books.mapNotNull { it.author?.personId })
        val personById = persons.associateBy { it.id }
        val bookDtoList = books.map {
            val author = it.author?.personId?.let { id -> personById[id] }
            BookDto.createFrom(it, author)
        }
        return BookCollectionDto(bookDtoList, pageCount)
    }
}