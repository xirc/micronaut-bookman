package micronaut.bookman.query.infra

import io.micronaut.context.annotation.Primary
import micronaut.bookman.exceptions.AppIllegalArgumentException
import micronaut.bookman.infra.DatabaseTrait
import micronaut.bookman.infra.schema.PersonTable
import micronaut.bookman.query.PersonSearchQueryResult
import micronaut.bookman.query.PersonSearchQueryResultSet
import micronaut.bookman.query.PersonSearchQueryService
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.lang.IllegalArgumentException
import javax.inject.Singleton
import javax.sql.DataSource
import kotlin.math.ceil

@Singleton
@Primary
class DBPersonSearchQueryService(
        private val source: DataSource
) : PersonSearchQueryService, DatabaseTrait {

    private fun buildSearchQuery(query: String): Query {
        return PersonTable.selectAll()
                .orWhere { PersonTable.firstName like "%$query%" }
                .orWhere { PersonTable.lastName like "%$query%" }
                .orderBy(PersonTable.updatedDate, SortOrder.DESC)
    }

    // TODO (HACK) THIS IMPLEMENTATION IS NAIVE and AWFUL. DONT in USE PRODUCTION.
    override fun searchAll(query: String, page: Long): PersonSearchQueryResultSet {
        if (page < 0) throw AppIllegalArgumentException("page should be positive or zero.")
        if (query.isBlank()) throw AppIllegalArgumentException("query should be non blank.")
        return withUtcZone {
            transaction(Database.connect(source)) {
                val itemCount = buildSearchQuery(query)
                        .limit(
                                PersonSearchQueryService.PageSize * PersonSearchQueryService.MaxPageCount,
                                PersonSearchQueryService.PageSize * (page+1)
                        )
                        .count()
                val pageCount = ceil(itemCount / PersonSearchQueryService.PageSize.toDouble()).toLong()
                val persons = buildSearchQuery(query)
                        .limit(PersonSearchQueryService.PageSize, PersonSearchQueryService.PageSize * page)
                        .map {
                            PersonSearchQueryResult(
                                    it[PersonTable.id],
                                    it[PersonTable.firstName],
                                    it[PersonTable.lastName],
                                    it[PersonTable.createdDate],
                                    it[PersonTable.updatedDate]
                            )
                        }
                PersonSearchQueryResultSet(persons, pageCount)
            }
        }
    }
}