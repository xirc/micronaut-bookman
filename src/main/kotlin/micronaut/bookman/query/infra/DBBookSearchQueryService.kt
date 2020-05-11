package micronaut.bookman.query.infra

import io.micronaut.context.annotation.Primary
import micronaut.bookman.exceptions.AppIllegalArgumentException
import micronaut.bookman.infra.DatabaseTrait
import micronaut.bookman.infra.schema.BookAuthorTable
import micronaut.bookman.infra.schema.BookTable
import micronaut.bookman.infra.schema.PersonTable
import micronaut.bookman.query.BookSearchQueryResult
import micronaut.bookman.query.BookSearchQueryResultSet
import micronaut.bookman.query.BookSearchQueryService
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import javax.inject.Singleton
import javax.sql.DataSource
import kotlin.math.ceil

@Singleton
@Primary
class DBBookSearchQueryService(
        private val source: DataSource
): BookSearchQueryService, DatabaseTrait {
    data class BookValue(val id: String, val title: String, val createdDate: DateTime, val updatedDate: DateTime)
    data class BookAuthorValue(val bookId: String, val personId: String, val firstName: String, val lastName: String)

    /*
    TODO (HACK) THIS IMPLEMENTATION IS NAIVE and AWFUL.
     DONT in USE PRODUCTION.
     */

    private fun buildSearchQuery(query: String): Query {
        return (BookTable leftJoin BookAuthorTable leftJoin PersonTable)
                .slice(BookTable.id, BookTable.updatedDate)
                .selectAll()
                .orWhere { BookTable.title like "%$query%" }
                .orWhere { PersonTable.firstName like "%$query%" }
                .orWhere { PersonTable.lastName like "%$query%" }
                .withDistinct(true)
                .orderBy(BookTable.updatedDate, SortOrder.DESC)
    }

    override fun searchAll(query: String, page: Long): BookSearchQueryResultSet {
        if (page < 0) throw AppIllegalArgumentException("page should be positive or zero.")
        if (query.isBlank()) throw AppIllegalArgumentException("query should be non blank")
        return withUtcZone {
            transaction (Database.connect(source)) {
                val itemCount = buildSearchQuery(query)
                        .limit(
                                BookSearchQueryService.PageSize * BookSearchQueryService.MaxPageCount,
                                BookSearchQueryService.PageSize * (page + 1)
                        ).count()
                val pageCount = ceil(itemCount / BookSearchQueryService.PageSize.toDouble()).toLong()
                val bookIds = buildSearchQuery(query)
                        .limit(BookSearchQueryService.PageSize, BookSearchQueryService.PageSize * page)
                        .map { it[BookTable.id] }
                val books = BookTable.selectAll()
                        .orWhere { BookTable.id inList bookIds }
                        .map {
                            BookValue(
                                    it[BookTable.id],
                                    it[BookTable.title],
                                    it[BookTable.createdDate],
                                    it[BookTable.updatedDate]
                            )
                        }
                val authors = (BookAuthorTable innerJoin PersonTable)
                        .slice(BookAuthorTable.book_id, PersonTable.id, PersonTable.firstName, PersonTable.lastName)
                        .selectAll()
                        .orWhere { BookAuthorTable.book_id inList bookIds }
                        .map {
                            BookAuthorValue(
                                    it[BookAuthorTable.book_id],
                                    it[PersonTable.id],
                                    it[PersonTable.firstName],
                                    it[PersonTable.lastName]
                            )
                        }
                val authorsByBookId = mutableMapOf<String, MutableList<BookAuthorValue>>()
                for (author in authors) {
                    authorsByBookId
                            .getOrPut(author.bookId, { mutableListOf() })
                            .add(author)
                }
                val results = books.map {
                    BookSearchQueryResult(
                            it.id,
                            it.title,
                            authorsByBookId[it.id]?.map { author ->
                                BookSearchQueryResult.BookAuthor(
                                        author.personId,
                                        author.firstName,
                                        author.lastName
                                )
                            } ?: emptyList(),
                            it.createdDate,
                            it.updatedDate

                    )
                }
                BookSearchQueryResultSet(results, pageCount)
            }
        }
    }

}