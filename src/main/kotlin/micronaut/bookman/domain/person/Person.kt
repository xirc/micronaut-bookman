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

    fun updateName(newName: FullName) {
        name = newName
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