package micronaut.bookman.usecase

import micronaut.bookman.domain.person.FullName
import micronaut.bookman.domain.person.Person
import micronaut.bookman.domain.person.PersonRepository

class LibrarianPersonUseCase(
        private val factory: Person.Factory,
        private val repository: PersonRepository
) {
    fun getPerson(id: String): Person {
        return repository.get(id)
    }

    fun createPerson(name: FullName): Person {
        val person = factory.create(name)
        repository.post(person)
        return person
    }

    fun deletePerson(id: String) {
        repository.delete(id)
    }

    fun patchPerson(id: String, firstName: String?, lastName: String?): Person {
        val person = repository.get(id)
        firstName?.run {
            person.updateFirstName(firstName)
        }
        lastName?.run {
            person.updateLastName(lastName)
        }
        repository.put(person)
        return person
    }
}