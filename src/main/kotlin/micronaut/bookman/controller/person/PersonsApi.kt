package micronaut.bookman.controller.person

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.*
import micronaut.bookman.controller.UnitResponse

interface PersonsApi {
    @Post("/")
    fun create(request: CreatePersonRequest): HttpResponse<PersonResponse>
    @Get("/{id}")
    fun get(@PathVariable id: String): HttpResponse<PersonResponse>
    @Delete("/{id}")
    fun delete(@PathVariable id: String): HttpResponse<UnitResponse>
    @Patch("/{id}")
    fun patch(@PathVariable id: String, request: PatchPersonRequest): HttpResponse<PersonResponse>
}