package micronaut.bookman.usecase

import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.shouldThrow
import io.micronaut.test.annotation.MicronautTest
import micronaut.bookman.SpecWithDataSource
import micronaut.bookman.domain.person.FullName
import micronaut.bookman.domain.person.Person
import micronaut.bookman.domain.person.PersonRepository
import micronaut.bookman.domain.person.exceptions.NoPersonException
import micronaut.bookman.exceptions.AppIllegalArgumentException
import java.util.*
import javax.sql.DataSource

@MicronautTest
class LibrarianPersonUseCaseTest(
        private val source: DataSource,
        private val factory: Person.Factory,
        private val repository: PersonRepository
): SpecWithDataSource(source, {
    val useCase = LibrarianPersonUseCase(
            factory,
            repository
    )

    fun createFixtures(n: Int): List<PersonDto> {
        val fixtures = mutableListOf<PersonDto>()
        for (i in 0 until n) {
            val person = useCase.createPerson("first$i", "last$i")
            fixtures.add(person)
        }
        return fixtures
    }

    "Librarian can create a person" {
        val person = useCase.createPerson("Harry", "Potter")
        person.firstName shouldBe "Harry"
        person.lastName shouldBe "Potter"
    }

    "Librarian can create a person with empty name" {
        val person = useCase.createPerson()
        person.firstName shouldBe ""
        person.lastName shouldBe ""
    }

    "Librarian should create persons that have different IDs" {
        val name = FullName("Harry", "Potter")
        val person1 = useCase.createPerson(name.firstName, name.lastName)
        val person2 = useCase.createPerson(name.firstName, name.lastName)
        person1.id shouldNotBe person2.id
    }

    "Librarian can get a person" {
        val person = useCase.createPerson("Harry", "Potter")
        val referencePerson = useCase.getPerson(person.id)
        referencePerson.id shouldBe person.id
        referencePerson.firstName shouldBe person.firstName
        referencePerson.lastName shouldBe person.lastName
    }

    "Librarian cannot get a person with invalid ID" {
        val id = UUID.randomUUID().toString()
        shouldThrow<NoPersonException> {
            useCase.getPerson(id)
        }
    }

    "Librarian can delete a person" {
        val person = useCase.createPerson("Harry", "Potter")
        useCase.deletePerson(person.id)
    }

    "Librarian cannot delete a person with invalid ID" {
        val id = UUID.randomUUID().toString()
        shouldThrow<NoPersonException> {
            useCase.deletePerson(id)
        }
    }

    "Librarian can update first name of a person" {
        val name = FullName("Harry", "Potter")
        val person = useCase.createPerson(name.firstName, name.lastName)
        val newFirstName = "first"
        val newPerson = useCase.patchPerson(person.id, newFirstName, null)
        newPerson.id shouldBe person.id
        newPerson.firstName shouldBe newFirstName
        newPerson.lastName shouldBe name.lastName
    }

    "Librarian can update last name of a person" {
        val name = FullName("Harry", "Potter")
        val person = useCase.createPerson(name.firstName, name.lastName)
        val newLastName = "last"
        val newPerson = useCase.patchPerson(person.id, null, newLastName)
        newPerson.id shouldBe person.id
        newPerson.firstName shouldBe name.firstName
        newPerson.lastName shouldBe newLastName
    }

    "Librarian cannot update name of a person with invalid ID" {
        val id = UUID.randomUUID().toString()
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

    "Librarian can list persons" {
        // 3 pages
        createFixtures(PersonRepository.PageSize * 2 + 1)
        val personsInPage1 = useCase.listPerson(1)
        personsInPage1.persons.size shouldBe PersonRepository.PageSize
        personsInPage1.pageCount shouldBe 1
    }

    "Librarian cannot list persons with invalid page" {
        shouldThrow<AppIllegalArgumentException> {
            useCase.listPerson(-1)
        }
    }

})