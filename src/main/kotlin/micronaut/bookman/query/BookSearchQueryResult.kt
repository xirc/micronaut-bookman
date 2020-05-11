package micronaut.bookman.query

import com.fasterxml.jackson.annotation.JsonInclude
import org.joda.time.DateTime

data class BookSearchQueryResult(
        val bookId: String,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        val title: String,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        val authors: List<BookAuthor>,
        val createdDate: DateTime,
        val updatedDate: DateTime
) {
    data class BookAuthor(
            val personId: String,
            @JsonInclude(JsonInclude.Include.NON_NULL)
            val firstName: String,
            @JsonInclude(JsonInclude.Include.NON_NULL)
            val lastName: String
    )
}