package micronaut.bookman.query

import com.fasterxml.jackson.annotation.JsonInclude
import org.joda.time.DateTime

data class PersonSearchQueryResult(
        val id: String,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        val firstName: String,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        val lastName: String,
        val createdDate: DateTime,
        val updatedDate: DateTime
)