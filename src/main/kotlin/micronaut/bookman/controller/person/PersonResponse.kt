package micronaut.bookman.controller.person

import micronaut.bookman.controller.ErrorResponseBody
import micronaut.bookman.domain.person.Person

data class PersonResponse private constructor(val value: PersonResponseBody?, val error: ErrorResponseBody?) {
    companion object {
        fun success(value: Person) = PersonResponse(PersonResponseBody.createFrom(value), null)
        fun failure(error: ErrorResponseBody) = PersonResponse(null, error)
    }
}