package micronaut.bookman.controller

import io.kotlintest.matchers.collections.shouldBeOneOf
import io.kotlintest.matchers.collections.shouldContain
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.micronaut.http.HttpStatus
import io.kotlintest.specs.StringSpec
import io.micronaut.test.annotation.MicronautTest
import micronaut.bookman.controller.book.BooksClient
import micronaut.bookman.controller.book.CreateBookRequest
import micronaut.bookman.controller.book.PatchBookRequest
import micronaut.bookman.domain.book.BookRepository
import java.util.*

@MicronautTest
class BooksControllerTest(
        private val client: BooksClient,
        private val bookFixture: BookFixtureClient,
        private val personFixture: PersonFixtureClient
): StringSpec({

    "BookController can create a book" {
        val response = client.create(CreateBookRequest())
        response.status shouldBe HttpStatus.OK
        response.body()!!.run {
            error shouldBe null
            value shouldNotBe null
        }
    }

    "BookController can create a book with title" {
        val title = "title ${UUID.randomUUID()}"
        val response = client.create(CreateBookRequest(title))
        response.status shouldBe HttpStatus.OK
        response.body()!!.run {
            error shouldBe null
            value shouldNotBe null
            value?.title shouldBe title
        }
    }

    "BookController should create books that have different IDs" {
        val response1 = client.create(CreateBookRequest("title"))
        val response2 = client.create(CreateBookRequest("title"))
        val book1 = response1.body.get().value!!
        val book2 = response2.body.get().value!!
        assert(book1.id != book2.id)
    }

    "BookController can get a book with ID" {
        val book = bookFixture.create()
        val response = client.get(book.id)
        response.status shouldBe HttpStatus.OK
        response.body()!!.run {
            error shouldBe null
            value shouldNotBe null
            value?.id shouldBe book.id
            value?.title shouldBe book.title
        }
    }

    "BookController cannot get a book with invalid ID" {
        val id = UUID.randomUUID().toString()
        val response = client.get(id)
        response.status shouldBe HttpStatus.OK
        response.body()!!.run {
            value shouldBe null
            error shouldNotBe null
            error?.id shouldBe ErrorCode.BOOK_NOT_FOUND
        }
    }

    "BookController can delete a book" {
        val book = bookFixture.create()
        val response = client.delete(book.id)
        response.status shouldBe HttpStatus.OK
        response.body()!!.run {
            error shouldBe null
        }
    }

    "BookController cannot delete a book with invalid ID" {
        val id = UUID.randomUUID().toString()
        val response = client.delete(id)
        response.status shouldBe HttpStatus.OK
        response.body()!!.run {
            error shouldNotBe null
            error?.id shouldBe ErrorCode.BOOK_NOT_FOUND
        }
    }

    "Book Controller can update a book title" {
        val book = bookFixture.create()
        val newTitle = "new book title"
        val response = client.patch(book.id, PatchBookRequest(newTitle, null))
        response.status shouldBe HttpStatus.OK
        response.body()!!.run {
            error shouldBe null
            value shouldNotBe null
            value?.id shouldBe book.id
            value?.title shouldBe newTitle
        }
    }

    "Book Controller cannot update a book title with invalid ID" {
        val id = UUID.randomUUID().toString()
        val response = client.patch(id, PatchBookRequest("new title", null))
        response.status shouldBe HttpStatus.OK
        response.body()!!.run {
            value shouldBe null
            error shouldNotBe null
            error?.id shouldBe ErrorCode.BOOK_NOT_FOUND
        }
    }

    "BookController can update author of a book" {
        val book = bookFixture.create()
        val person = personFixture.create()
        val response = client.patch(book.id, PatchBookRequest(null, listOf(person.id)))
        response.status shouldBe HttpStatus.OK
        response.body()!!.run {
            error shouldBe null
            value shouldNotBe null
            value?.id shouldBe book.id
            val authors = value!!.authors
            authors.size shouldBe 1
            authors.map { it.id } shouldContain  person.id
        }
    }

    "BookController cannot update author of a book to invalid one" {
        val book = bookFixture.create()
        val personId = UUID.randomUUID().toString()
        val response = client.patch(book.id, PatchBookRequest(null, listOf(personId)))
        response.status shouldBe HttpStatus.OK
        response.body()!!.run {
            value shouldBe null
            error shouldNotBe null
            error?.id shouldBe ErrorCode.PERSON_NOT_FOUND
        }
    }

    "BookController can list books" {
        // 3 pages
        bookFixture.createCollection(BookRepository.PageSize * 3 + 1)
        val response = client.list(1)
        response.status shouldBe HttpStatus.OK
        response.body()!!.run {
            error shouldBe null
            value shouldNotBe null
            value?.pageCount shouldBe 2
            value?.books?.size shouldBe BookRepository.PageSize
        }
    }

})