package micronaut.bookman.usecase

import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.shouldThrow
import io.micronaut.test.annotation.MicronautTest
import micronaut.bookman.SpecWithDataSource
import micronaut.bookman.domain.book.Book
import micronaut.bookman.domain.book.error.NoBookException
import micronaut.bookman.domain.time.ServerDateTimeFactory
import micronaut.bookman.infra.book.DBBookRepository
import java.util.*
import javax.sql.DataSource

@MicronautTest
class LibrarianBookUseCaseTest(private val source: DataSource) : SpecWithDataSource(source, {
    val factory = Book.Factory(ServerDateTimeFactory())
    val useCase = LibrarianBookUseCase(
            factory,
            DBBookRepository(source, factory)
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
        val referenceBook = useCase.getBook(book.id)
        referenceBook.title shouldBe title
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
})