package micronaut.bookman.domain.person

import micronaut.bookman.domain.person.exceptions.IllegalPersonStateException
import micronaut.bookman.domain.time.DateTimeFactory
import org.joda.time.DateTime
import javax.inject.Singleton

class Person private constructor(
        private val timeFactory: DateTimeFactory,
        val id: PersonId,
        val createdDate: DateTime
) {
    var name: FullName = DefaultName
        private set
    var updatedDate: DateTime = createdDate
        private set(value) {
            if (value.isBefore(createdDate)) throw IllegalPersonStateException("UpdatedDate should be after CreatedDate.")
            field = value
        }

    fun updateName(name: FullName) {
        this.name = name
        updatedDate = timeFactory.now()
    }
    fun updateFirstName(firstName: String) {
        name = name.copy(firstName = firstName)
        updatedDate = timeFactory.now()
    }
    fun updateLastName(lastName: String) {
        name = name.copy(lastName = lastName)
        updatedDate = timeFactory.now()
    }

    @Singleton
    class Factory(private val timeFactory: DateTimeFactory) {
        fun create(): Person = Person(
                timeFactory,
                PersonId(),
                timeFactory.now()
        )
        fun createFromRepository(
                id: PersonId,
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

    companion object {
        val DefaultName = FullName("", "")
    }
}