package micronaut.bookman.controller.person

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import micronaut.bookman.exceptions.PersonErrorResponseSyntax.toResponseBody
import micronaut.bookman.controller.UnitResponse
import micronaut.bookman.domain.person.exceptions.NoPersonException
import micronaut.bookman.query.PersonSearchQueryService
import micronaut.bookman.usecase.LibrarianPersonUseCase

@Controller("/persons")
class PersonsController(
        private val useCase: LibrarianPersonUseCase,
        private val queryService: PersonSearchQueryService
) : PersonsApi {

    override fun create(request: CreatePersonRequest): HttpResponse<PersonResponse> {
        val person = useCase.createPerson(request.firstName, request.lastName)
        val body = PersonResponse.success(person)
        return HttpResponse.ok(body)
    }

    override fun get(id: String): HttpResponse<PersonResponse> {
        return try {
            val person = useCase.getPerson(id)
            val body = PersonResponse.success(person)
            HttpResponse.ok(body)
        } catch (e: NoPersonException) {
            val body = PersonResponse.failure(e.toResponseBody())
            HttpResponse.ok(body)
        }
    }

    override fun delete(id: String): HttpResponse<UnitResponse> {
        return try {
            useCase.deletePerson(id)
            val body = UnitResponse.success()
            HttpResponse.ok(body)
        } catch (e: NoPersonException) {
            val body = UnitResponse.failure(e.toResponseBody())
            HttpResponse.ok(body)
        }
    }

    override fun patch(id: String, request: PatchPersonRequest): HttpResponse<PersonResponse> {
        return try {
            val person = useCase.patchPerson(id, request.firstName, request.lastName)
            val body = PersonResponse.success(person)
            HttpResponse.ok(body)
        } catch (e: NoPersonException) {
            val body = PersonResponse.failure(e.toResponseBody())
            HttpResponse.ok(body)
        }
    }

    override fun list(
            page: Int?
    ): HttpResponse<PersonCollectionResponse> {
        val persons = useCase.listPerson(page?.toLong() ?: 0L)
        val body = PersonCollectionResponse.success(persons)
        return HttpResponse.ok(body)
    }

    override fun search(query: String, page: Int?): HttpResponse<PersonSearchResponse> {
        val persons = queryService.searchAll(query, page?.toLong() ?: 0L)
        val body = PersonSearchResponse.success(persons)
        return HttpResponse.ok(body)
    }

}