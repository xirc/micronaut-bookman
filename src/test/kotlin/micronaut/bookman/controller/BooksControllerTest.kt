package micronaut.bookman.controller

import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.micronaut.http.HttpStatus
import io.kotlintest.specs.StringSpec
import io.micronaut.context.ApplicationContext
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.test.annotation.MicronautTest
import micronaut.bookman.controller.book.BooksClient
import micronaut.bookman.controller.book.CreateBookRequest
import micronaut.bookman.controller.book.PatchBookRequest
import micronaut.bookman.controller.person.CreatePersonRequest
import micronaut.bookman.controller.person.PersonsClient
import micronaut.bookman.domain.book.BookRepository
import micronaut.bookman.usecase.BookDto
import micronaut.bookman.usecase.PersonDto
import java.util.*

@MicronautTest
class BooksControllerTest(ctx: ApplicationContext): StringSpec({
    val embeddedServer: EmbeddedServer = ctx.getBean(EmbeddedServer::class.java)
    val client = embeddedServer.applicationContext.getBean(BooksClient::class.java)
    val personClient = embeddedServer.applicationContext.getBean(PersonsClient::class.java)

    // TODO リファクタしたほうがよい
    fun createFixture(): BookDto {
        val title = "title ${UUID.randomUUID()}"
        val response = client.create(CreateBookRequest(title))
        return response.body()?.value!!
    }
    fun createPerson(): PersonDto {
        val firstName = "first"
        val lastName = "last"
        val response = personClient.create(CreatePersonRequest(firstName, lastName))
        return response.body()?.value!!
    }
    fun createFixtures(n: Int): List<BookDto> {
        val fixtures = mutableListOf<BookDto>()
        for (i in 0 until n) {
            val response = client.create(CreateBookRequest("title$i"))
            fixtures.add(response.body()?.value!!)
        }
        return fixtures
    }

    "BookController can create a book" {
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
        val book = createFixture()
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
        val book = createFixture()
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
        val book = createFixture()
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
        val book = createFixture()
        val person = createPerson()
        val response = client.patch(book.id, PatchBookRequest(null, person.id))
        response.status shouldBe HttpStatus.OK
        response.body()!!.run {
            error shouldBe null
            value shouldNotBe null
            value?.id shouldBe book.id
            value?.author shouldNotBe null
            value?.author?.id shouldBe person.id
        }
    }

    // TODO BookController に 無効な著者を設定しようとしたときのテストがない

    "BookController can list books" {
        // 3 pages
        createFixtures(BookRepository.PageSize * 3 + 1)
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