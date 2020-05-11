package micronaut.bookman.query

import com.fasterxml.jackson.annotation.JsonInclude

data class BookSearchQueryResultSet(
        @JsonInclude(JsonInclude.Include.NON_NULL)
        val results: List<BookSearchQueryResult>,
        val pageCount: Long
)