package micronaut.bookman.controller.person

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import micronaut.bookman.controller.PersonErrorResponseSyntax.toResponseBody
import micronaut.bookman.controller.UnitResponse
import micronaut.bookman.domain.person.FullName
import micronaut.bookman.domain.person.Person
import micronaut.bookman.domain.person.error.NoPersonException
import micronaut.bookman.domain.time.ServerDateTimeFactory
import micronaut.bookman.infra.person.DBPersonRepository
import micronaut.bookman.usecase.LibrarianPersonUseCase
import javax.sql.DataSource

@Controller("/persons")
class PersonsController(val source: DataSource) : PersonsApi {
    private val factory = Person.Factory(ServerDateTimeFactory())
    private val repository = DBPersonRepository(source, factory)
    private val useCase = LibrarianPersonUseCase(factory, repository)

    override fun create(request: CreatePersonRequest): HttpResponse<PersonResponse> {
        val person = useCase.createPerson(
                FullName(request.firstName, request.lastName)
        )
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
}