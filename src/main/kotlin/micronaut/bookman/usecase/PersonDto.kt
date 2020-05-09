package micronaut.bookman.usecase

import micronaut.bookman.domain.person.Person
import org.joda.time.DateTime

data class PersonDto private constructor(
        val id: String,
        val firstName: String,
        val lastName: String,
        val createdDate: DateTime,
        val updatedDate: DateTime
) {
    companion object {
        fun createFrom(person: Person) = PersonDto(
                person.id,
                person.name.firstName,
                person.name.lastName,
                person.createdDate,
                person.updatedDate
        )
    }
}