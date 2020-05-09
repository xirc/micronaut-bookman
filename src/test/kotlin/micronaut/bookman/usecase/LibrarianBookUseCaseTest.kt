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
        val res = useCase.createBook(title)
        res.book.title shouldBe title
    }

    "Librarian should create books that have different IDs" {
        val res1 = useCase.createBook("TITLE ${UUID.randomUUID()}")
        val res2 = useCase.createBook("TITLE ${UUID.randomUUID()}")
        res1.book.id shouldNotBe res2.book.id
    }

    "Librarian can get a book" {
        val title = "TITLE ${UUID.randomUUID()}"
        val res = useCase.createBook(title)
        val newRes = useCase.getBook(res.book.id)
        newRes.book.title shouldBe title
    }

    "Librarian cannot get a book with invalid ID" {
        val id = UUID.randomUUID().toString()
        shouldThrow<NoBookException> {
            useCase.getBook(id)
        }
    }

    "Librarian can delete a book" {
        val res = useCase.createBook("TITLE ${UUID.randomUUID()}")
        useCase.deleteBook(res.book.id)
    }

    "Librarian cannot delete a book with invalid ID" {
        val id = UUID.randomUUID().toString()
        shouldThrow<NoBookException> {
            useCase.deleteBook(id)
        }
    }

    "Librarian can update a title of a book" {
        val res = useCase.createBook("title")
        val newTitle = "new book title"
        val newRes = useCase.patchBook(res.book.id, newTitle)
        newRes.book.id shouldBe newRes.book.id
        newRes.book.title shouldBe newTitle
    }

    "Librarian cannot update a title with invalid ID" {
        val id = UUID.randomUUID().toString()
        shouldThrow<NoBookException> {
            useCase.patchBook(id, "new title")
        }
    }

    "Librarian can update author of a book" {
        val bookRes = useCase.createBook("book title")
        val person = personUseCase.createPerson(FullName("Harry", "Potter"))
        val newRes = useCase.patchBook(bookRes.book.id, authorId = person.id)
        newRes.book.author shouldNotBe null
        newRes.book.author?.personId shouldBe person.id
    }

    "Librarian can update nothing" {
        val book = useCase.createBook("a book").book
        val person = personUseCase.createPerson(FullName("abc", "def"))
        useCase.patchBook(book.id, null, person.id)
    }

})