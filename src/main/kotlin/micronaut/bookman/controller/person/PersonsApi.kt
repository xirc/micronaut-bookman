package micronaut.bookman.controller.person

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.*
import micronaut.bookman.controller.UnitResponse
import javax.validation.constraints.PositiveOrZero

interface PersonsApi {
    @Post("/")
    fun create(request: CreatePersonRequest): HttpResponse<PersonResponse>
    @Get("/{id}")
    fun get(@PathVariable id: String): HttpResponse<PersonResponse>
    @Delete("/{id}")
    fun delete(@PathVariable id: String): HttpResponse<UnitResponse>
    @Patch("/{id}")
    fun patch(@PathVariable id: String, request: PatchPersonRequest): HttpResponse<PersonResponse>

    @Get("/")
    fun list(
            @QueryValue @PositiveOrZero page: Int?
    ): HttpResponse<PersonCollectionResponse>

    @Get("/search")
    fun search(
            @QueryValue query: String,
            @QueryValue @PositiveOrZero page: Int?
    ): HttpResponse<PersonSearchResponse>
}