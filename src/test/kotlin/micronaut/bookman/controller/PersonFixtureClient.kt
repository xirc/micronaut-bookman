package micronaut.bookman.controller

import micronaut.bookman.controller.person.CreatePersonRequest
import micronaut.bookman.controller.person.PersonsClient
import micronaut.bookman.usecase.PersonDto
import java.util.*
import javax.inject.Singleton

@Singleton
class PersonFixtureClient(
        private val client: PersonsClient
) {
    fun create(): PersonDto {
        val q = UUID.randomUUID().toString()
        val firstName = "f$q"
        val lastName = "l$q"
        val response = client.create(CreatePersonRequest(firstName, lastName))
        return response.body()?.value!!
    }
    fun createCollection(n: Int): List<PersonDto> {
        val fixtures = mutableListOf<PersonDto>()
        for (i in 0 until n) {
            val q = UUID.randomUUID().toString()
            val firstName = "f$i$q"
            val lastName = "l$i$q"
            val response = client.create(CreatePersonRequest(firstName, lastName))
            fixtures.add(response.body()?.value!!)
        }
        return fixtures
    }
}