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
        val authors = personRepository.getAll(book.authors.map { it.personId })
        return BookDto.createFrom(book, authors)
    }

    fun createBook(title: String? = null): BookDto {
        val book = factory.create()
        book.updateTitle(title ?: "")
        val savedBook = repository.save(book)
        return BookDto.createFrom(savedBook, emptyList())
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
            book.updateAuthors(listOf(BookAuthor(authorId))) // TODO
        }
        val updatedBook = repository.update(book)
        val authors = personRepository.getAll(book.authors.map { it.personId })
        return BookDto.createFrom(updatedBook, authors)
    }

    fun listBook(
            page: Int
    ): BookCollectionDto {
        if (page < 0) throw IllegalArgumentException("page should be positive or zero.")
        val books = repository.getPage(page.toLong())
        val pageCount = repository.countPage(page.toLong())
        val persons = personRepository.getAll(books.flatMap { it.authors.map { author -> author.personId } })
        var personById = persons.associateBy { it.id }
        val bookDtoList = books.map {
            val authors = it.authors.mapNotNull { author -> personById[author.personId] }
            BookDto.createFrom(it, authors)
        }
        return BookCollectionDto(bookDtoList, pageCount)
    }
}