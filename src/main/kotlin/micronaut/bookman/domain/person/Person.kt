package micronaut.bookman.domain.person

import micronaut.bookman.domain.person.error.IllegalPersonStateException
import micronaut.bookman.domain.time.DateTimeFactory
import org.joda.time.DateTime
import java.util.*

class Person private constructor(
        private val timeFactory: DateTimeFactory,
        val id: String,
        val createdDate: DateTime
) {
    var name: FullName = FullName("", "")
        private set
    var updatedDate: DateTime = createdDate
        private set(value) {
            if (value.isBefore(createdDate)) throw IllegalPersonStateException("UpdatedDate should be after CreatedDate.")
            field = value
        }

    fun updateFirstName(firstName: String) {
        name = name.copy(firstName = firstName)
        updatedDate = timeFactory.now()
    }
    fun updateLastName(lastName: String) {
        name = name.copy(lastName = lastName)
        updatedDate = timeFactory.now()
    }

    class Factory(private val timeFactory: DateTimeFactory) {
        fun create(name: FullName): Person = Person(
                timeFactory,
                UUID.randomUUID().toString(),
                timeFactory.now()
        ).apply {
            this.name = name
        }
        fun createFromRepository(
                id: String,
                name: FullName,
                createdDate: DateTime,
                updatedDate: DateTime
        ) = Person(
                timeFactory,
                id,
                createdDate
        ).apply {
            this.name = name
            this.updatedDate = updatedDate
        }
    }
}