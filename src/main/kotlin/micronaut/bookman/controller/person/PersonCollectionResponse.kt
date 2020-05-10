package micronaut.bookman.controller.person

import micronaut.bookman.controller.ErrorResponseBody
import micronaut.bookman.usecase.PersonCollectionDto

data class PersonCollectionResponse private constructor(
        val value: PersonCollectionDto?,
        val error: ErrorResponseBody?
) {
    companion object {
        fun success(value: PersonCollectionDto) = PersonCollectionResponse(value, null)
        fun failure(error: ErrorResponseBody) = PersonCollectionResponse(null, error)
    }
}