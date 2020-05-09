package micronaut.bookman.infra

import io.kotlintest.shouldBe
import io.kotlintest.shouldNotThrowAny
import io.kotlintest.shouldThrow
import io.micronaut.test.annotation.MicronautTest
import micronaut.bookman.SpecWithDataSource
import micronaut.bookman.domain.book.Book
import micronaut.bookman.domain.book.error.NoBookException
import micronaut.bookman.domain.time.ServerDateTimeFactory
import micronaut.bookman.infra.book.DBBookRepository
import micronaut.bookman.infra.error.InfraException
import java.util.*
import javax.sql.DataSource

@MicronautTest
class DBBookRepositoryTest(private val source: DataSource) : SpecWithDataSource(source, {
    val factory = Book.Factory(ServerDateTimeFactory())
    val repository = DBBookRepository(source, factory)

    fun createFixture(): Book {
        val book = factory.create()
        repository.post(book)
        return book
    }

    "DBBookRepository can post a book" {
        val book = factory.create()
        repository.post(book)
    }

    "DBBookRepository cannot create a post twice" {
        val book = factory.create()
        shouldNotThrowAny {
            repository.post(book)
        }
        shouldThrow<InfraException> {
            repository.post(book)
        }
    }

    "DBBookRepository can get a book" {
        val book = createFixture()
        val referenceBook = repository.get(book.id)
        referenceBook.run {
            id shouldBe book.id
            title shouldBe book.title
            createdDate shouldBe book.createdDate
            updatedDate shouldBe book.updatedDate
        }
    }

    "DBBookRepository cannot get a book with invalid ID" {
        val id = UUID.randomUUID().toString()
        shouldThrow<NoBookException> {
            repository.get(id)
        }
    }

    "DBBookRepository can delete a book" {
        val book = createFixture()
        repository.delete(book.id)
        shouldThrow<NoBookException> {
            repository.get(book.id)
        }
    }

    "DBBookRepository cannot delete a book with invalid ID" {
        val book = factory.create()
        shouldThrow<NoBookException> {
            repository.get(book.id)
        }
        shouldThrow<NoBookException> {
            repository.delete(book.id)
        }
    }
})