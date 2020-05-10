package micronaut.bookman.infra

import io.kotlintest.matchers.collections.shouldBeSortedWith
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotThrowAny
import io.kotlintest.shouldThrow
import io.micronaut.test.annotation.MicronautTest
import micronaut.bookman.SpecWithDataSource
import micronaut.bookman.domain.person.FullName
import micronaut.bookman.domain.person.Person
import micronaut.bookman.domain.person.PersonRepository
import micronaut.bookman.domain.person.error.DuplicatePersonException
import micronaut.bookman.domain.person.error.NoPersonException
import micronaut.bookman.infra.person.DBPersonRepository
import java.lang.IllegalArgumentException
import java.util.*
import javax.sql.DataSource

@MicronautTest
class DBPersonRepositoryTest(
        private val source: DataSource,
        private val factory: Person.Factory
) : SpecWithDataSource(source, {
    val repository = DBPersonRepository(source, factory)

    fun createFixture(): Person {
        val person = factory.create(FullName("Harry", "Potter"))
        repository.save(person)
        return person
    }
    fun createPersonFixtures(n: Int): List<Person> {
        // TODO Batch Insert する
        return (0 until n).map {
            val person = factory.create(FullName("First$it", "Last$it"))
            repository.save(person)
        }
    }

    "DBPersonRepository can create a person" {
        val person = factory.create(FullName("Harry", "Potter"))
        repository.save(person)
    }

    "DBPersonRepository cannot create a person twice" {
        val person = factory.create(FullName("Harry", "Potter"))
        shouldNotThrowAny {
            repository.save(person)
        }
        shouldThrow<DuplicatePersonException> {
            repository.save(person)
        }
    }

    "DBPersonRepository can get a person" {
        val person = createFixture()
        val referencePerson = repository.get(person.id)
        referencePerson.run {
            id shouldBe person.id
            name shouldBe person.name
            createdDate shouldBe  person.createdDate
            updatedDate shouldBe person.updatedDate
        }
    }

    "DBPersonRepository cannot get a person with invalid ID" {
        val id = UUID.randomUUID().toString()
        shouldThrow<NoPersonException> {
            repository.get(id)
        }
    }

    "DBPersonRepository can update a person" {
        val person = createFixture()
        person.updateFirstName("Ronald")
        person.updateLastName("Weasley")
        repository.update(person)
        val newPerson = repository.get(person.id)
        newPerson.run {
            id shouldBe person.id
            name shouldBe person.name
            createdDate shouldBe person.createdDate
            updatedDate shouldBe person.updatedDate
        }
    }

    "DBPersonRepository cannot update a person with invalid ID" {
        val person = factory.create(FullName("Harry", "Potter"))
        shouldThrow<NoPersonException> {
            repository.update(person)
        }
    }

    "DBPersonRepository can delete a person" {
        val person = createFixture()
        shouldNotThrowAny {
            repository.delete(person.id)
        }
        shouldThrow<NoPersonException> {
            repository.get(person.id)
        }
    }

    "DBPersonRepository cannot delete a person with invalid ID" {
        val person = factory.create(FullName("Harry", "Potter"))
        shouldThrow<NoPersonException> {
            repository.get(person.id)
        }
        shouldThrow<NoPersonException> {
            repository.delete(person.id)
        }
    }

    "DBPersonRepository can get a page" {
        createPersonFixtures(PersonRepository.PageSize + 1)
        val personsInPage0 = repository.getPage(0)
        personsInPage0.size shouldBe PersonRepository.PageSize
        personsInPage0 shouldBeSortedWith compareByDescending { it.updatedDate }
        val personsInPage1 = repository.getPage(1)
        personsInPage1.size shouldBe 1
        personsInPage1 shouldBeSortedWith compareByDescending { it.updatedDate }
    }

    "DBPersonRepository cannot get a page with invalid page number." {
        shouldThrow<IllegalArgumentException> {
            repository.getPage(-1)
        }
    }

    "DBPersonRepository can get empty page" {
        repository.getPage(0).size shouldBe 0
    }

    "DBPersonRepository can get page count" {
        // The repository has (MaxPageCount + 2) pages.
        createPersonFixtures(PersonRepository.PageSize * (PersonRepository.MaxPageCount + 1) + 1)
        repository.countPage(0) shouldBe PersonRepository.MaxPageCount
        repository.countPage(1) shouldBe PersonRepository.MaxPageCount
        repository.countPage(2) shouldBe PersonRepository.MaxPageCount - 1
        repository.countPage(3) shouldBe PersonRepository.MaxPageCount - 2
        repository.countPage(PersonRepository.MaxPageCount.toLong()) shouldBe 1
        repository.countPage(PersonRepository.MaxPageCount + 1L) shouldBe 0
    }

})