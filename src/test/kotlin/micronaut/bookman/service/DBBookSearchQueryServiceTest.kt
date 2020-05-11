package micronaut.bookman.service

import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.micronaut.test.annotation.MicronautTest
import micronaut.bookman.SpecWithDataSource
import micronaut.bookman.domain.book.BookAuthor
import micronaut.bookman.domain.person.FullName
import micronaut.bookman.exceptions.AppIllegalArgumentException
import micronaut.bookman.infra.DBBookFixture
import micronaut.bookman.infra.DBPersonFixture
import micronaut.bookman.infra.book.DBBookRepository
import micronaut.bookman.infra.person.DBPersonRepository
import micronaut.bookman.query.infra.DBBookSearchQueryService
import javax.sql.DataSource

@MicronautTest
class DBBookSearchQueryServiceTest(
        private val source: DataSource,
        private val bookFixture: DBBookFixture,
        private val personFixture: DBPersonFixture,
        private val bookRepository: DBBookRepository,
        private val personRepository: DBPersonRepository
): SpecWithDataSource(source, {
    val queryService = DBBookSearchQueryService(source)

    "DBBookSearchQueryService can search books by title" {
        val books = bookFixture.createCollection(100)
        for (i in 0 until 100) {
            books[i].updateTitle("title $i")
            bookRepository.update(books[i])
        }

        val res1 = queryService.searchAll("0", 0)
        res1.pageCount shouldBe 0
        res1.results.size shouldBe 10

        val res2 = queryService.searchAll("le 2", 0)
        res2.pageCount shouldBe 0
        res2.results.size shouldBe 11

        val res3 = queryService.searchAll("title", 1)
        res3.pageCount shouldBe 2
        res3.results.size shouldBe 25
    }

    "DBBookSearchQueryService can search books by author first name" {
        val books = bookFixture.createCollection(200)
        for (book in books) {
            book.updateTitle("")
            bookRepository.update(book)
        }
        val persons = personFixture.createCollection(100)
        for ((i,person) in persons.withIndex()) {
            person.updateName(FullName("first $i", ""))
            personRepository.update(person)
        }
        for (i in 0 until 100) {
            books[i].updateAuthors(listOf(BookAuthor(persons[i].id)))
            bookRepository.update(books[i])
        }

        val res1 = queryService.searchAll("0", 0)
        res1.pageCount shouldBe 0
        res1.results.size shouldBe 10

        val res2 = queryService.searchAll("first 2", 0)
        res2.pageCount shouldBe 0
        res2.results.size shouldBe 11

        val res3 = queryService.searchAll("first", 1)
        res3.pageCount shouldBe 2
        res3.results.size shouldBe 25
    }

    "DBBookSearchQueryService cannot search books with invalid page" {
        shouldThrow<AppIllegalArgumentException> {
            queryService.searchAll("abc", -1)
        }
    }

    "DBBookSearchQueryService cannot search books with blank query" {
        shouldThrow<AppIllegalArgumentException> {
            queryService.searchAll("   ", 0)
        }
    }

})