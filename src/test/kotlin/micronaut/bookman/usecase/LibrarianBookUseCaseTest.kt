package micronaut.bookman.usecase

import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.shouldThrow
import io.micronaut.test.annotation.MicronautTest
import micronaut.bookman.SpecWithDataSource
import micronaut.bookman.domain.book.Book
import micronaut.bookman.domain.book.BookRepository
import micronaut.bookman.domain.book.error.NoBookException
import micronaut.bookman.domain.person.FullName
import micronaut.bookman.domain.person.Person
import micronaut.bookman.domain.person.PersonRepository
import micronaut.bookman.domain.time.ServerDateTimeFactory
import java.util.*
import javax.sql.DataSource

@MicronautTest
class LibrarianBookUseCaseTest(
        private val source: DataSource,
        private val factory: Book.Factory,
        private val personFactory: Person.Factory,
        private val repository: BookRepository,
        private val personRepository: PersonRepository
) : SpecWithDataSource(source, {
    val useCase = LibrarianBookUseCase(
            factory,
            repository,
            personRepository
    )
    val personUseCase = LibrarianPersonUseCase(
            Person.Factory(ServerDateTimeFactory()),
            personRepository
    )

    "Librarian can create a book" {
        val title = "Book (${UUID.randomUUID()})"
        val book = useCase.createBook(title)
        book.title shouldBe title
    }

    "Librarian should create books that have different IDs" {
        val book1 = useCase.createBook("TITLE ${UUID.randomUUID()}")
        val book2 = useCase.createBook("TITLE ${UUID.randomUUID()}")
        book1.id shouldNotBe book2.id
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
        val person = personUseCase.createPerson(FullName("Harry", "Potter"))
        val newBook = useCase.patchBook(book.id, authorId = person.id)
        newBook.author shouldNotBe null
        newBook.author?.id shouldBe person.id
    }

    "Librarian can update nothing" {
        val book = useCase.createBook("a book")
        val person = personUseCase.createPerson(FullName("abc", "def"))
        useCase.patchBook(book.id, null, person.id)
    }

})