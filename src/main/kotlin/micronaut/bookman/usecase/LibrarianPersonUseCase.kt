package micronaut.bookman.usecase

import micronaut.bookman.domain.person.FullName
import micronaut.bookman.domain.person.Person
import micronaut.bookman.domain.person.PersonRepository
import java.util.*

class LibrarianPersonUseCase(
        private val repository: PersonRepository
) {
    fun getPerson(id: UUID): Person {
        return repository.get(id)
    }

    fun createPerson(name: FullName): Person {
        val person = Person.create(name)
        return repository.save(person)
    }

    fun deletePerson(id: UUID) {
        repository.delete(id)
    }

    fun patchPerson(id: UUID, firstName: String?, lastName: String?): Person {
        val person = repository.get(id)
        var newName = person.name
        firstName?.run {
            newName = newName.copy(firstName = firstName)
        }
        lastName?.run {
            newName = newName.copy(lastName = lastName)
        }
        person.updateName(newName)
        return repository.update(person)
    }
}