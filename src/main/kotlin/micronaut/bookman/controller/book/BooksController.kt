package micronaut.bookman.controller.book

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.*
import micronaut.bookman.controller.ResponseBody
import micronaut.bookman.usecase.BookCollectionDto
import micronaut.bookman.usecase.BookDto
import micronaut.bookman.usecase.LibrarianBookUseCase

@Controller("/books")
class BooksController(
        private val useCase: LibrarianBookUseCase
) : BooksApi {

    override fun create(request: CreateBookRequest): HttpResponse<ResponseBody<BookDto>> {
        val book = useCase.createBook(request.title, request.authorIds)
        val body = ResponseBody.success(book)
        return HttpResponse.ok(body)
    }

    override fun get(id: String): HttpResponse<ResponseBody<BookDto>> {
        val book = useCase.getBook(id)
        val body = ResponseBody.success(book)
        return HttpResponse.ok(body)
    }

    override fun delete(id: String): HttpResponse<ResponseBody<Unit>> {
        useCase.deleteBook(id)
        val body = ResponseBody.success(Unit)
        return HttpResponse.ok(body)
    }

    override fun patch(id: String, request: PatchBookRequest): HttpResponse<ResponseBody<BookDto>> {
        val book = useCase.patchBook(id, request.title, request.authorIds)
        val body = ResponseBody.success(book)
        return HttpResponse.ok(body)
    }

    override fun list(page: Int?): HttpResponse<ResponseBody<BookCollectionDto>> {
        val books = useCase.listBook(page ?: 0)
        val body = ResponseBody.success(books)
        return HttpResponse.ok(body)
    }

}