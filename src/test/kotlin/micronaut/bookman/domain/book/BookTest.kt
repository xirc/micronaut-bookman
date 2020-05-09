package micronaut.bookman.domain.book

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import io.micronaut.test.annotation.MicronautTest
import java.util.*

@MicronautTest
class BookTest : StringSpec({
    "Book has empty default title" {
        val book = Book.create()
        book.title shouldBe ""
    }

    "Book can change its title" {
        val book = Book.create()
        val title = "TITLE (${UUID.randomUUID()})"
        book.updateTitle(title)
        book.title shouldBe title
    }
})