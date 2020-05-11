package micronaut.bookman.controller.person

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import micronaut.bookman.controller.ResponseBody
import micronaut.bookman.query.PersonSearchQueryResultSet
import micronaut.bookman.query.PersonSearchQueryService
import micronaut.bookman.usecase.LibrarianPersonUseCase
import micronaut.bookman.usecase.PersonCollectionDto
import micronaut.bookman.usecase.PersonDto

@Controller("/persons")
class PersonsController(
        private val useCase: LibrarianPersonUseCase,
        private val queryService: PersonSearchQueryService
) : PersonsApi {

    override fun create(request: CreatePersonRequestBody): HttpResponse<ResponseBody<PersonDto>> {
        val person = useCase.createPerson(request.firstName, request.lastName)
        val body = ResponseBody.success(person)
        return HttpResponse.ok(body)
    }

    override fun get(id: String): HttpResponse<ResponseBody<PersonDto>> {
        val person = useCase.getPerson(id)
        val body = ResponseBody.success(person)
        return HttpResponse.ok(body)
    }

    override fun delete(id: String): HttpResponse<ResponseBody<Unit>> {
        useCase.deletePerson(id)
        val body = ResponseBody.success(Unit)
        return HttpResponse.ok(body)
    }

    override fun patch(id: String, request: PatchPersonRequestBody): HttpResponse<ResponseBody<PersonDto>> {
        val person = useCase.patchPerson(id, request.firstName, request.lastName)
        val body = ResponseBody.success(person)
        return HttpResponse.ok(body)
    }

    override fun list(
            page: Int?
    ): HttpResponse<ResponseBody<PersonCollectionDto>> {
        val persons = useCase.listPerson(page?.toLong() ?: 0L)
        val body = ResponseBody.success(persons)
        return HttpResponse.ok(body)
    }

    override fun search(query: String, page: Int?): HttpResponse<ResponseBody<PersonSearchQueryResultSet>> {
        val persons = queryService.searchAll(query, page?.toLong() ?: 0L)
        val body = ResponseBody.success(persons)
        return HttpResponse.ok(body)
    }

}