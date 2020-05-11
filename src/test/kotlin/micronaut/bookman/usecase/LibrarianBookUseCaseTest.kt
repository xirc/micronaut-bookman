package micronaut.bookman.usecase

import io.kotlintest.matchers.collections.shouldContain
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.shouldThrow
import io.micronaut.test.annotation.MicronautTest
import micronaut.bookman.SpecWithDataSource
import micronaut.bookman.domain.book.Book
import micronaut.bookman.domain.book.BookRepository
import micronaut.bookman.domain.book.exceptions.NoBookException
import micronaut.bookman.domain.person.PersonRepository
import micronaut.bookman.domain.person.exceptions.NoPersonException
import java.util.*
import javax.sql.DataSource

@MicronautTest
class LibrarianBookUseCaseTest(
        private val source: DataSource,
        private val factory: Book.Factory,
        private val repository: BookRepository,
        private val personRepository: PersonRepository,
        private val personUseCase: LibrarianPersonUseCase
) : SpecWithDataSource(source, {
    val useCase = LibrarianBookUseCase(
            factory,
            repository,
            personRepository
    )

    fun createFixtures(n: Int): List<BookDto> {
        val fixtures = mutableListOf<BookDto>()
        for (i in 0 until n) {
            val book = useCase.createBook("title $i")
            fixtures.add(book)
        }
        return fixtures
    }

    "Librarian can create a book" {
        useCase.createBook()
    }

    "Librarian can create a book with title" {
        val title = "Book (${UUID.randomUUID()})"
        val book = useCase.createBook(title)
        book.title shouldBe title
    }

    "Librarian should create books that have different IDs" {
        val book1 = useCase.createBook("TITLE ${UUID.randomUUID()}")
        val book2 = useCase.createBook("TITLE ${UUID.randomUUID()}")
        book1.id shouldNotBe book2.id
    }

    "Librarian can create a book with authors" {
        val person1 = personUseCase.createPerson()
        val person2 = personUseCase.createPerson()
        val book = useCase.createBook(authorIds = listOf(person1.id, person2.id))
        book.authors.size shouldBe 2
        book.authors.map { it.id } shouldContain person1.id
        book.authors.map { it.id } shouldContain person2.id
    }

    "Librarian can get a book" {
        val title = "TITLE ${UUID.randomUUID()}"
        val book = useCase.createBook(title)
        val fetchedBook = useCase.getBook(book.id)
        fetchedBook.title shouldBe title
    }

    "Librarian cannot get a book with invalid ID" {
        val id = UUID.randomUUID().toString()
        shouldThrow<NoBookException> {
            useCase.getBook(id)
        }
    }

    "Librarian can delete a book" {
        val book = useCase.createBook("TITLE ${UUID.randomUUID()}")
        useCase.deleteBook(book.id)
    }

    "Librarian cannot delete a book with invalid ID" {
        val id = UUID.randomUUID().toString()
        shouldThrow<NoBookException> {
            useCase.deleteBook(id)
        }
    }

    "Librarian can update a title of a book" {
        val book = useCase.createBook("title")
        val newTitle = "new book title"
        val newBook = useCase.patchBook(book.id, newTitle)
        newBook.id shouldBe book.id
        newBook.title shouldBe newTitle
    }

    "Librarian cannot update a title with invalid ID" {
        val id = UUID.randomUUID().toString()
        shouldThrow<NoBookException> {
            useCase.patchBook(id, "new title")
        }
    }

    "Librarian can update author of a book" {
        val book = useCase.createBook("book title")
        val person1 = personUseCase.createPerson("Harry", "Potter")
        val person2 = personUseCase.createPerson("Rubeus", "Hagrid")
        val newBook = useCase.patchBook(book.id, authorIds = listOf(person1.id, person2.id))
        newBook.authors.size shouldBe 2
        newBook.authors.map { it.id } shouldContain person1.id
        newBook.authors.map { it.id } shouldContain person2.id
    }

    "Librarian cannot update author of a book to invalid one." {
        val book = useCase.createBook("book title")
        val personId = UUID.randomUUID().toString()
        shouldThrow<NoPersonException> {
            useCase.patchBook(book.id, null, listOf(personId))
        }
    }

    "Librarian can update nothing" {
        val book = useCase.createBook("a book")
        val person = personUseCase.createPerson("first", "last")
        useCase.patchBook(book.id, authorIds = listOf(person.id))
        val newBook = useCase.patchBook(book.id, null, null)
        newBook.id shouldBe book.id
        newBook.title shouldBe "a book"
        newBook.authors.map { it.id } shouldContain person.id
    }

    "Librarian can list books" {
        // 3 pages
        createFixtures(BookRepository.PageSize * 2 + 1)
        val booksInPage1 = useCase.listBook(1)
        booksInPage1.books.size shouldBe BookRepository.PageSize
        booksInPage1.pageCount shouldBe 1
    }

})