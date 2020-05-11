package micronaut.bookman.query

import com.fasterxml.jackson.annotation.JsonInclude

data class PersonSearchQueryResultSet(
        @JsonInclude(JsonInclude.Include.NON_NULL)
        val results: List<PersonSearchQueryResult>,
        val pageCount: Long
)