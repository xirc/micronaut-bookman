package micronaut.bookman.domain.person

import org.joda.time.DateTime
import java.util.*

class Person private constructor(
        val id: UUID,
        val createdDate: DateTime,
        val updatedDate: DateTime
) {
    var name: FullName = FullName("", "")
        private set

    fun updateFirstName(firstName: String) {
        name = name.copy(firstName = firstName)
    }
    fun updateLastName(lastName: String) {
        name = name.copy(lastName = lastName)
    }

    companion object {
        fun create(name: FullName) = Person(
                UUID.randomUUID(),
                DateTime.now(),
                DateTime.now()
        ).apply {
            this.name = name
        }
        fun createFromRepository(
                id: UUID,
                createdDate: DateTime,
                updatedDate: DateTime,
                name: FullName
        ) = Person(
                id,
                createdDate,
                updatedDate
        ).apply {
            this.name = name
        }
    }
}