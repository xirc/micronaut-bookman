package micronaut.bookman.usecase

import micronaut.bookman.domain.person.FullName
import micronaut.bookman.domain.person.Person
import micronaut.bookman.domain.person.PersonRepository
import javax.inject.Singleton

@Singleton
class LibrarianPersonUseCase(
        private val factory: Person.Factory,
        private val repository: PersonRepository
) {
    fun getPerson(id: String): PersonDto {
        val person = repository.get(id)
        return PersonDto.createFrom(person)
    }

    fun createPerson(name: FullName): PersonDto {
        val person = factory.create(name)
        repository.save(person)
        return PersonDto.createFrom(person)
    }

    fun deletePerson(id: String) {
        repository.delete(id)
    }

    fun patchPerson(id: String, firstName: String?, lastName: String?): PersonDto {
        val person = repository.get(id)
        firstName?.run {
            person.updateFirstName(firstName)
        }
        lastName?.run {
            person.updateLastName(lastName)
        }
        repository.update(person)
        return PersonDto.createFrom(person)
    }
}