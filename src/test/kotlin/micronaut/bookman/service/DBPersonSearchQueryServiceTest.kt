package micronaut.bookman.service

import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.micronaut.test.annotation.MicronautTest
import micronaut.bookman.SpecWithDataSource
import micronaut.bookman.domain.person.FullName
import micronaut.bookman.exceptions.AppIllegalArgumentException
import micronaut.bookman.infra.DBPersonFixture
import micronaut.bookman.infra.person.DBPersonRepository
import micronaut.bookman.query.infra.DBPersonSearchQueryService
import javax.sql.DataSource

@MicronautTest
class DBPersonSearchQueryServiceTest(
        private val source: DataSource,
        private val fixture: DBPersonFixture,
        private val repository: DBPersonRepository
): SpecWithDataSource(source, {
    val queryService = DBPersonSearchQueryService(source)

    "DBPersonSearchQueryService can search first name" {
        val persons = fixture.createCollection(100)
        val names = listOf(
                "a", "ab", "abc", "abcd", "abcde",
                "abcdef", "abcdefg", "abcdefgh", "abcdefghi", "abcdefghij"
        )
        for ((i,person) in persons.withIndex()) {
            val firstName = names[i.rem(names.size)]
            person.updateName(FullName(firstName, ""))
            repository.update(person)
        }

        val resA = queryService.searchAll("a", 0)
        resA.results.size shouldBe 25
        resA.pageCount shouldBe 3L

        val resB = queryService.searchAll("cdef", 0)
        resB.results.size shouldBe 25
        resB.pageCount shouldBe 1L

        val resC = queryService.searchAll("cdef", 1)
        resC.results.size shouldBe 25
        resC.pageCount shouldBe 0L

        val resD = queryService.searchAll("cdefghi", 0)
        resD.results.size shouldBe 20
        resD.pageCount shouldBe 0L
    }

    "DBPersonSearchQueryService can search last name" {
        val persons = fixture.createCollection(100)
        val names = listOf(
                "a", "ab", "abc", "abcd", "abcde",
                "abcdef", "abcdefg", "abcdefgh", "abcdefghi", "abcdefghij"
        )
        for ((i,person) in persons.withIndex()) {
            val lastName = names[i.rem(names.size)]
            person.updateName(FullName("", lastName))
            repository.update(person)
        }

        val res = queryService.searchAll("cdef", 0)
        res.results.size shouldBe 25
        res.pageCount shouldBe 1L
    }

    "DBPersonSearchQueryService cannot search with invalid page" {
        shouldThrow<AppIllegalArgumentException> {
            queryService.searchAll("abc", -1)
        }
    }

    "DBPersonSearchQueryService cannot search with blank query" {
        shouldThrow<AppIllegalArgumentException> {
            queryService.searchAll("   ", 0)
        }
    }

})