package micronaut.bookman.controller

import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.micronaut.http.HttpStatus
import io.micronaut.test.annotation.MicronautTest
import micronaut.bookman.SpecWithDataSource
import micronaut.bookman.controller.person.CreatePersonRequestBody
import micronaut.bookman.controller.person.PatchPersonRequestBody
import micronaut.bookman.controller.person.PersonsClient
import micronaut.bookman.domain.person.PersonRepository
import micronaut.bookman.exceptions.ErrorCode
import java.util.*
import javax.sql.DataSource

@MicronautTest
class PersonsControllerTest(
        private val source: DataSource,
        private val client: PersonsClient,
        private val personFixture: PersonFixtureClient
): SpecWithDataSource(source, {

    "PersonController can create a person" {
        val response = client.create(CreatePersonRequestBody())
        response.status shouldBe HttpStatus.OK
        response.body()!!.run {
            error shouldBe null
            value shouldNotBe null
        }
    }

    "PersonController can create a person with name" {
        val firstName = "first ${UUID.randomUUID()}"
        val lastName = "last ${UUID.randomUUID()}"
        val response = client.create(CreatePersonRequestBody(firstName, lastName))
        response.status shouldBe HttpStatus.OK
        response.body()!!.run {
            error shouldBe null
            value shouldNotBe null
            value?.firstName shouldBe firstName
            value?.lastName shouldBe lastName
        }
    }

    "PersonController should create persons that have different IDs" {
        val response1 = client.create(CreatePersonRequestBody("Harry", "Potter"))
        val response2 = client.create(CreatePersonRequestBody("Harry", "Potter"))
        val person1 = response1.body.get().value!!
        val person2 = response2.body.get().value!!
        assert(person1.id != person2.id)
    }

    "PersonController can get a person with ID" {
        val person = personFixture.create()
        val response = client.get(person.id)
        response.status shouldBe HttpStatus.OK
        response.body()!!.run {
            error shouldBe null
            value shouldNotBe null
            value?.id shouldBe person.id
            value?.firstName shouldBe person.firstName
            value?.lastName shouldBe person.lastName
        }
    }

    "PersonController cannot get a person with invalid ID" {
        val id = UUID.randomUUID().toString()
        val response = client.get(id)
        response.status shouldBe HttpStatus.OK
        response.body()!!.run {
            value shouldBe null
            error shouldNotBe null
            error?.id shouldBe ErrorCode.PERSON_NOT_FOUND
        }
    }

    "PersonController can delete a person" {
        val person = personFixture.create()
        val response = client.delete(person.id)
        response.status shouldBe HttpStatus.OK
        response.body()!!.run {
            error shouldBe null
        }
    }

    "Person Controller cannot delete a person with invalid ID" {
        val id = UUID.randomUUID().toString()
        val response = client.delete(id)
        response.status shouldBe HttpStatus.OK
        response.body()!!.run {
            error shouldNotBe null
            error?.id shouldBe ErrorCode.PERSON_NOT_FOUND
        }
    }

    "PersonController can update name of a person" {
        val person = personFixture.create()
        val newFirstName = "firstName"
        val newLastName = "lastName"
        val response = client.patch(person.id, PatchPersonRequestBody(newFirstName, newLastName))
        response.status shouldBe HttpStatus.OK
        response.body()!!.run {
            error shouldBe null
            value shouldNotBe null
            value?.id shouldBe person.id
            value?.firstName shouldBe newFirstName
            value?.lastName shouldBe newLastName
        }
    }

    "PersonController cannot update name of person with invalid ID" {
        val id = UUID.randomUUID().toString()
        val response = client.patch(id, PatchPersonRequestBody("first", "last"))
        response.status shouldBe HttpStatus.OK
        response.body()!!.run {
            value shouldBe null
            error shouldNotBe null
            error?.id shouldBe ErrorCode.PERSON_NOT_FOUND
        }
    }

    "PersonController can list persons" {
        // 4 pages
        personFixture.createCollection(PersonRepository.PageSize * 3 + 1)
        val response = client.list(1)
        response.status shouldBe HttpStatus.OK
        response.body()!!.run {
            error shouldBe null
            value shouldNotBe null
            value?.pageCount shouldBe 2
            value?.persons?.size shouldBe PersonRepository.PageSize
        }
    }

    "PersonController can search persons" {
        // 4 pages
        personFixture.createCollection(PersonRepository.PageSize * 3 + 1)
        val response = client.search("f", 1 )
        response.status shouldBe HttpStatus.OK
        response.body()!!.run {
            error shouldBe null
            value shouldNotBe null
            value?.pageCount shouldBe 2
            value?.results?.size shouldBe PersonRepository.PageSize
        }
    }

})