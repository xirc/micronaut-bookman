package micronaut.bookman.controller

import micronaut.bookman.controller.book.BooksClient
import micronaut.bookman.controller.book.CreateBookRequestBody
import micronaut.bookman.usecase.BookDto
import java.util.*
import javax.inject.Singleton

@Singleton
class BookFixtureClient(
        private val client: BooksClient
) {
    fun create(): BookDto {
        val title = "t${UUID.randomUUID()}"
        val response = client.create(CreateBookRequestBody(title))
        return response.body()?.value!!
    }

    fun createCollection(n: Int): List<BookDto> {
        val fixtures = mutableListOf<BookDto>()
        for (i in 0 until n) {
            val title = "t$i${UUID.randomUUID()}"
            val response = client.create(CreateBookRequestBody(title))
            fixtures.add(response.body()?.value!!)
        }
        return fixtures
    }
}