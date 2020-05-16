package micronaut.bookman.usecase

import micronaut.bookman.domain.book.Book
import micronaut.bookman.domain.book.BookAuthor
import micronaut.bookman.domain.book.BookRepository
import micronaut.bookman.domain.person.PersonId
import micronaut.bookman.domain.person.PersonRepository
import micronaut.bookman.exceptions.AppIllegalArgumentException
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

    fun createBook(title: String? = null, authorIds: List<String>? = null): BookDto {
        val book = factory.create()
        if (title != null) {
            book.updateTitle(title)
        }
        authorIds?.map { PersonId.fromString(it) }?.also {
            book.updateAuthors(it.map { id -> BookAuthor(id) })
        }
        val savedBook = repository.save(book)
        val authors = personRepository.getAll(savedBook.authors.map { it.personId })
        return BookDto.createFrom(savedBook, authors)
    }

    fun deleteBook(id: String) {
        repository.delete(id)
    }

    fun patchBook(
            id: String,
            title: String? = null,
            authorIds: List<String>? = null
    ): BookDto {
        val book = repository.get(id)
        if (title != null) {
            book.updateTitle(title)
        }
        if (authorIds != null) {
            book.updateAuthors(authorIds.map {
                PersonId.fromString(it)
            }.map {
                BookAuthor(it)
            })
        }
        val updatedBook = repository.update(book)
        val authors = personRepository.getAll(updatedBook.authors.map { it.personId })
        return BookDto.createFrom(updatedBook, authors)
    }

    fun listBook(
            page: Long
    ): BookCollectionDto {
        if (page < 0) throw AppIllegalArgumentException("page should be positive or zero.")
        val books = repository.getPage(page)
        val pageCount = repository.countPage(page)
        val persons = personRepository.getAll(books.flatMap { it.authors.map { author -> author.personId } })
        var personById = persons.associateBy { it.id }
        val bookDtoList = books.map {
            val authors = it.authors.mapNotNull { author -> personById[author.personId] }
            BookDto.createFrom(it, authors)
        }
        return BookCollectionDto(bookDtoList, pageCount)
    }
}