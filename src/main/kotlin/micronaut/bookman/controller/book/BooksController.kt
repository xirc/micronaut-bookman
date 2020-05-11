package micronaut.bookman.controller.book

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.*
import micronaut.bookman.controller.ApplicationExceptionSyntax.toResponseBody
import micronaut.bookman.controller.UnitResponse
import micronaut.bookman.domain.book.exceptions.NoBookException
import micronaut.bookman.domain.person.exceptions.NoPersonException
import micronaut.bookman.usecase.LibrarianBookUseCase

@Controller("/books")
class BooksController(
        private val useCase: LibrarianBookUseCase
) : BooksApi {

    override fun create(request: CreateBookRequest): HttpResponse<BookResponse> {
        val book = useCase.createBook(request.title, request.authorIds)
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
            val book = useCase.patchBook(id, request.title, request.authorIds)
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

    override fun list(page: Int?): HttpResponse<BookCollectionResponse> {
        val books = useCase.listBook(page ?: 0)
        val body = BookCollectionResponse.success(books)
        return HttpResponse.ok(body)
    }

}