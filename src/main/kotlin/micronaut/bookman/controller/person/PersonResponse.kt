package micronaut.bookman.controller.person

import micronaut.bookman.controller.ErrorResponseBody
import micronaut.bookman.domain.person.Person
import micronaut.bookman.usecase.PersonDto

data class PersonResponse private constructor(val value: PersonDto?, val error: ErrorResponseBody?) {
    companion object {
        fun success(value: PersonDto) = PersonResponse(value, null)
        fun failure(error: ErrorResponseBody) = PersonResponse(null, error)
    }
}