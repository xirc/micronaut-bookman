package micronaut.bookman.controller.book

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.*
import micronaut.bookman.controller.UnitResponse
import javax.validation.constraints.PositiveOrZero

interface BooksApi {
    @Post("/")
    fun create(request: CreateBookRequest): HttpResponse<BookResponse>
    @Get("/{id}")
    fun get(@PathVariable id: String): HttpResponse<BookResponse>
    @Delete("/{id}")
    fun delete(@PathVariable id: String): HttpResponse<UnitResponse>
    @Patch("/{id}")
    fun patch(@PathVariable id: String, request: PatchBookRequest): HttpResponse<BookResponse>

    @Get("/")
    fun list(
            @PositiveOrZero @QueryValue page: Int?
    ): HttpResponse<BookCollectionResponse>
}