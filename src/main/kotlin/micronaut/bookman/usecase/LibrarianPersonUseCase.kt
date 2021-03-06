package micronaut.bookman.usecase

import micronaut.bookman.domain.person.Person
import micronaut.bookman.domain.person.PersonId
import micronaut.bookman.domain.person.PersonRepository
import micronaut.bookman.exceptions.AppIllegalArgumentException
import javax.inject.Singleton

@Singleton
class LibrarianPersonUseCase(
        private val factory: Person.Factory,
        private val repository: PersonRepository
) {
    fun getPerson(id: String): PersonDto {
        val person = repository.get(PersonId.fromString(id))
        return PersonDto.createFrom(person)
    }

    fun createPerson(firstName: String? = null, lastName: String? = null): PersonDto {
        val person = factory.create()
        firstName?.also {
            person.updateFirstName(it)
        }
        lastName?.also {
            person.updateLastName(it)
        }
        repository.save(person)
        return PersonDto.createFrom(person)
    }

    fun deletePerson(id: String) {
        repository.delete(PersonId.fromString(id))
    }

    fun patchPerson(id: String, firstName: String?, lastName: String?): PersonDto {
        val person = repository.get(PersonId.fromString(id))
        firstName?.run {
            person.updateFirstName(firstName)
        }
        lastName?.run {
            person.updateLastName(lastName)
        }
        repository.update(person)
        return PersonDto.createFrom(person)
    }

    fun listPerson(
            page: Long
    ): PersonCollectionDto {
        if (page < 0) throw AppIllegalArgumentException("page should be positive or zero.")
        val persons = repository.getPage(page)
        val pageCount = repository.countPage(page)
        return PersonCollectionDto(
                persons.map { PersonDto.createFrom(it) },
                pageCount
        )
    }
}