package micronaut.bookman.controller.book

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.*
import micronaut.bookman.controller.ResponseBody
import micronaut.bookman.usecase.BookCollectionDto
import micronaut.bookman.usecase.BookDto
import javax.validation.constraints.PositiveOrZero

interface BooksApi {
    @Post("/")
    fun create(request: CreateBookRequest): HttpResponse<ResponseBody<BookDto>>
    @Get("/{id}")
    fun get(@PathVariable id: String): HttpResponse<ResponseBody<BookDto>>
    @Delete("/{id}")
    fun delete(@PathVariable id: String): HttpResponse<ResponseBody<Unit>>
    @Patch("/{id}")
    fun patch(@PathVariable id: String, request: PatchBookRequest): HttpResponse<ResponseBody<BookDto>>

    @Get("/")
    fun list(
            @PositiveOrZero @QueryValue page: Int?
    ): HttpResponse<ResponseBody<BookCollectionDto>>
}