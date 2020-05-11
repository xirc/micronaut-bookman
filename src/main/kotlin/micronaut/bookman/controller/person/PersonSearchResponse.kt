package micronaut.bookman.controller.person

import micronaut.bookman.controller.ErrorResponseBody
import micronaut.bookman.query.PersonSearchQueryResultSet

data class PersonSearchResponse private constructor(
        val value: PersonSearchQueryResultSet?,
        val error: ErrorResponseBody?
) {
    companion object {
        fun success(value: PersonSearchQueryResultSet) = PersonSearchResponse(value, null)
        fun failure(error: ErrorResponseBody) = PersonSearchResponse(null, error)
    }
}