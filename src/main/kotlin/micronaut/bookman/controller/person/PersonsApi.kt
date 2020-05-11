package micronaut.bookman.controller.person

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.*
import micronaut.bookman.controller.ResponseBody
import micronaut.bookman.query.PersonSearchQueryResultSet
import micronaut.bookman.usecase.PersonCollectionDto
import micronaut.bookman.usecase.PersonDto
import javax.validation.constraints.PositiveOrZero

interface PersonsApi {
    @Post("/")
    fun create(request: CreatePersonRequestBody): HttpResponse<ResponseBody<PersonDto>>
    @Get("/{id}")
    fun get(@PathVariable id: String): HttpResponse<ResponseBody<PersonDto>>
    @Delete("/{id}")
    fun delete(@PathVariable id: String): HttpResponse<ResponseBody<Unit>>
    @Patch("/{id}")
    fun patch(@PathVariable id: String, request: PatchPersonRequestBody): HttpResponse<ResponseBody<PersonDto>>

    @Get("/")
    fun list(
            @QueryValue @PositiveOrZero page: Int?
    ): HttpResponse<ResponseBody<PersonCollectionDto>>

    @Get("/search")
    fun search(
            @QueryValue query: String,
            @QueryValue @PositiveOrZero page: Int?
    ): HttpResponse<ResponseBody<PersonSearchQueryResultSet>>
}