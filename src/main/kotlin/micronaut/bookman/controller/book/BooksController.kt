package micronaut.bookman.controller.book

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.*
import micronaut.bookman.controller.BookErrorResponseSyntax.toResponseBody
import micronaut.bookman.controller.UnitResponse
import micronaut.bookman.domain.book.BookRepository
import micronaut.bookman.domain.book.error.NoBookException
import micronaut.bookman.usecase.LibrarianBookUseCase
import java.util.*

@Controller("/books")
class BooksController(
        private val repository: BookRepository
) : BooksApi {
    private val useCase = LibrarianBookUseCase(repository)

    override fun create(request: CreateBookRequest): HttpResponse<BookResponse> {
        val book = useCase.createBook(request.title)
        val body = BookResponse.success(book)
        return HttpResponse.ok(body)
    }

    override fun get(id: String): HttpResponse<BookResponse> {
        return try {
            val book = useCase.getBook(UUID.fromString(id))
            val body = BookResponse.success(book)
            HttpResponse.ok(body)
        } catch (e: NoBookException) {
            val body = BookResponse.failure(e.toResponseBody())
            HttpResponse.ok(body)
        }
    }

    override fun delete(id: String): HttpResponse<UnitResponse> {
        return try {
            useCase.deleteBook(UUID.fromString(id))
            val body = UnitResponse.success()
            HttpResponse.ok(body)
        } catch (e: NoBookException) {
            val body = UnitResponse.failure(e.toResponseBody())
            HttpResponse.ok(body)
        }
    }

    override fun patch(id: String, request: PatchBookRequest): HttpResponse<BookResponse> {
        return try {
            val book = useCase.patchBook(UUID.fromString(id), request.title)
            val body = BookResponse.success(book)
            HttpResponse.ok(body)
        } catch (e: NoBookException) {
            val body = BookResponse.failure(e.toResponseBody())
            HttpResponse.ok(body)
        }
    }
}