package micronaut.bookman.infra

import io.micronaut.core.annotation.Introspected
import io.micronaut.data.annotation.DateCreated
import io.micronaut.data.annotation.DateUpdated
import io.micronaut.data.annotation.MappedEntity
import micronaut.bookman.domain.person.FullName
import micronaut.bookman.domain.person.Person
import org.joda.time.DateTime
import java.time.Instant
import java.util.*
import javax.persistence.Id

@Introspected
@MappedEntity("person")
class PersonEntity {
    @Id
    var id: UUID = UUID.randomUUID()
    @DateCreated
    var createdDate: Instant = Instant.now()
    @DateUpdated
    var updatedDate: Instant = Instant.now()
    var firstName: String = ""
    var lastName: String = ""

    fun to(): Person = Person.createFromRepository(
            id,
            DateTime(createdDate.toEpochMilli()),
            DateTime(updatedDate.toEpochMilli()),
            FullName(firstName, lastName)
    )

    companion object {
        fun from(person: Person) = PersonEntity().apply {
            id = person.id
            createdDate = Instant.ofEpochMilli(person.createdDate.millis)
            updatedDate = Instant.ofEpochMilli(person.updatedDate.millis)
            firstName = person.name.firstName
            lastName = person.name.lastName
        }
    }
}