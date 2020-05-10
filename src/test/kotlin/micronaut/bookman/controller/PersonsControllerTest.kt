package micronaut.bookman.controller

import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.StringSpec
import io.micronaut.http.HttpStatus
import io.micronaut.test.annotation.MicronautTest
import micronaut.bookman.controller.person.CreatePersonRequest
import micronaut.bookman.controller.person.PatchPersonRequest
import micronaut.bookman.controller.person.PersonsClient
import micronaut.bookman.domain.person.PersonRepository
import java.util.*

@MicronautTest
class PersonsControllerTest(
    private val client: PersonsClient,
    private val personFixture: PersonFixtureClient
): StringSpec({

    "PersonController can create a person" {
        val firstName = "first ${UUID.randomUUID()}"
        val lastName = "last ${UUID.randomUUID()}"
        val response = client.create(CreatePersonRequest(firstName, lastName))
        response.status shouldBe HttpStatus.OK
        response.body()!!.run {
            error shouldBe null
            value shouldNotBe null
            value?.firstName shouldBe firstName
            value?.lastName shouldBe lastName
        }
    }

    "PersonController should create persons that have different IDs" {
        val response1 = client.create(CreatePersonRequest("Harry", "Potter"))
        val response2 = client.create(CreatePersonRequest("Harry", "Potter"))
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
        val response = client.patch(person.id, PatchPersonRequest(newFirstName, newLastName))
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
        val response = client.patch(id, PatchPersonRequest("first", "last"))
        response.status shouldBe HttpStatus.OK
        response.body()!!.run {
            value shouldBe null
            error shouldNotBe null
            error?.id shouldBe ErrorCode.PERSON_NOT_FOUND
        }
    }

    "PersonController can list persons" {
        // 3 pages
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

})