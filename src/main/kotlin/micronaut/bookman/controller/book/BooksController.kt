package micronaut.bookman.controller.book

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.*
import micronaut.bookman.controller.BookErrorResponseSyntax.toResponseBody
import micronaut.bookman.controller.PersonErrorResponseSyntax.toResponseBody
import micronaut.bookman.controller.UnitResponse
import micronaut.bookman.domain.book.Book
import micronaut.bookman.domain.book.error.NoBookException
import micronaut.bookman.domain.person.Person
import micronaut.bookman.domain.person.error.NoPersonException
import micronaut.bookman.domain.time.ServerDateTimeFactory
import micronaut.bookman.infra.book.DBBookRepository
import micronaut.bookman.infra.person.DBPersonRepository
import micronaut.bookman.usecase.LibrarianBookUseCase
import micronaut.bookman.usecase.LibrarianPersonUseCase
import javax.sql.DataSource

@Controller("/books")
class BooksController(private val source: DataSource) : BooksApi {
    private val factory = Book.Factory(ServerDateTimeFactory())
    private val repository = DBBookRepository(source, factory)
    private val personFactory = Person.Factory(ServerDateTimeFactory())
    private val personRepository = DBPersonRepository(source, personFactory)
    private val useCase = LibrarianBookUseCase(factory, repository, personRepository)
    private val personUseCase = LibrarianPersonUseCase(personFactory, personRepository)

    override fun create(request: CreateBookRequest): HttpResponse<BookResponse> {
        val book = useCase.createBook(request.title)
        val body = BookResponse.success(book)
        return HttpResponse.ok(body)
    }

    override fun get(id: String): HttpResponse<BookResponse> {
        return try {
            val book = useCase.getBook(id)
            val body = BookResponse.success(book)
            HttpResponse.ok(body)
        } catch (e: NoBookException) {
            val body = BookResponse.failure(e.toResponseBody())
            HttpResponse.ok(body)
        }
    }

    override fun delete(id: String): HttpResponse<UnitResponse> {
        return try {
            useCase.deleteBook(id)
            val body = UnitResponse.success()
            HttpResponse.ok(body)
        } catch (e: NoBookException) {
            val body = UnitResponse.failure(e.toResponseBody())
            HttpResponse.ok(body)
        }
    }

    override fun patch(id: String, request: PatchBookRequest): HttpResponse<BookResponse> {
        return try {
            val book = useCase.patchBook(id, request.title, request.authorId)
            val body = BookResponse.success(book)
            HttpResponse.ok(body)
        } catch (e: NoBookException) {
            val body = BookResponse.failure(e.toResponseBody())
            HttpResponse.ok(body)
        } catch (e: NoPersonException) {
            val body = BookResponse.failure(e.toResponseBody())
            HttpResponse.ok(body)
        }
    }
}