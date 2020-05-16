package micronaut.bookman.usecase

import com.fasterxml.jackson.annotation.JsonInclude
import micronaut.bookman.domain.person.Person
import org.joda.time.DateTime

data class PersonDto private constructor(
        val id: String,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        val firstName: String,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        val lastName: String,
        val createdDate: DateTime,
        val updatedDate: DateTime
) {
    companion object {
        fun createFrom(person: Person) = PersonDto(
                person.id.toString(),
                person.name.firstName,
                person.name.lastName,
                person.createdDate,
                person.updatedDate
        )
    }
}