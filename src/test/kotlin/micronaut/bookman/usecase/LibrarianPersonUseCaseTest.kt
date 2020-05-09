package micronaut.bookman.usecase

import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.shouldThrow
import io.micronaut.test.annotation.MicronautTest
import micronaut.bookman.RepositoryCollection
import micronaut.bookman.SpecWithDataSource
import micronaut.bookman.domain.person.FullName
import micronaut.bookman.domain.person.PersonRepository
import micronaut.bookman.domain.person.error.NoPersonException
import java.util.*

@MicronautTest
class LibrarianPersonUseCaseTest(
        private val repositoryCollection: RepositoryCollection,
        private val repository: PersonRepository
): SpecWithDataSource(repositoryCollection, {
    val useCase = LibrarianPersonUseCase(repository)

    "Librarian can create a person" {
        val name = FullName("Harry", "Potter")
        val person = useCase.createPerson(name)
        person.name shouldBe name
    }

    "Librarian should create persons that have different IDs" {
        val name = FullName("Harry", "Potter")
        val person1 = useCase.createPerson(name)
        val person2 = useCase.createPerson(name)
        person1.id shouldNotBe person2.id
    }

    "Librarian can get a person" {
        val person = useCase.createPerson(FullName("Harry", "Potter"))
        val referencePerson = useCase.getPerson(person.id)
        referencePerson.name shouldBe person.name
    }

    "Librarian cannot get a person with invalid ID" {
        val id = UUID.randomUUID()
        shouldThrow<NoPersonException> {
            useCase.getPerson(id)
        }
    }

    "Librarian can delete a person" {
        val person = useCase.createPerson(FullName("Harry", "Potter"))
        useCase.deletePerson(person.id)
    }

    "Librarian cannot delete a person with invalid ID" {
        val id = UUID.randomUUID()
        shouldThrow<NoPersonException> {
            useCase.deletePerson(id)
        }
    }

    "Librarian can update first name of a person" {
        val name = FullName("Harry", "Potter")
        val person = useCase.createPerson(name)
        val newFirstName = "first"
        val newPerson = useCase.patchPerson(person.id, newFirstName, null)
        newPerson.id shouldBe person.id
        newPerson.name.firstName shouldBe newFirstName
        newPerson.name.lastName shouldBe name.lastName
    }

    "Librarian can update last name of a person" {
        val name = FullName("Harry", "Potter")
        val person = useCase.createPerson(name)
        val newLastName = "last"
        val newPerson = useCase.patchPerson(person.id, null, newLastName)
        newPerson.id shouldBe person.id
        newPerson.name.firstName shouldBe name.firstName
        newPerson.name.lastName shouldBe newLastName
    }

    "Librarian cannot update name of a person with invalid ID" {
        val id = UUID.randomUUID()
        shouldThrow<NoPersonException> {
            useCase.patchPerson(id, "first", "last")
        }
        shouldThrow<NoPersonException> {
            useCase.patchPerson(id, "first", null)
        }
        shouldThrow<NoPersonException> {
            useCase.patchPerson(id, null, "last")
        }
    }

})