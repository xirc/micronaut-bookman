package micronaut.bookman.query

import com.fasterxml.jackson.annotation.JsonInclude

data class BookSearchQueryServiceResult(
        val bookId: String,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        val bookTitle: String,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        val authors: List<Author>
) {
    data class Author(
            val personId: String,
            @JsonInclude(JsonInclude.Include.NON_NULL)
            val firstName: String,
            @JsonInclude(JsonInclude.Include.NON_NULL)
            val lastName: String
    )
}