package micronaut.bookman.infra

import io.kotlintest.shouldBe
import io.kotlintest.shouldNotThrowAny
import io.kotlintest.shouldThrow
import io.micronaut.test.annotation.MicronautTest
import micronaut.bookman.SpecWithDataSource
import micronaut.bookman.domain.person.FullName
import micronaut.bookman.domain.person.Person
import micronaut.bookman.domain.person.error.DuplicatePersonException
import micronaut.bookman.domain.person.error.NoPersonException
import micronaut.bookman.domain.time.ServerDateTimeFactory
import micronaut.bookman.infra.person.DBPersonRepository
import java.util.*
import javax.sql.DataSource

@MicronautTest
class DBPersonRepositoryTest(private val source: DataSource) : SpecWithDataSource(source, {
    val factory = Person.Factory(ServerDateTimeFactory())
    val repository = DBPersonRepository(source, factory)

    fun createFixture(): Person {
        val person = factory.create(FullName("Harry", "Potter"))
        repository.save(person)
        return person
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

})