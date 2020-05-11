package micronaut.bookman.query.infra

import io.micronaut.context.annotation.Primary
import micronaut.bookman.query.BookSearchQueryService
import micronaut.bookman.query.BookSearchQueryServiceResultSet
import javax.inject.Singleton
import javax.sql.DataSource

@Singleton
@Primary
class DBBookSearchQueryService(
        val source: DataSource
) : BookSearchQueryService {
    override fun searchAll(q: String, page: Int): BookSearchQueryServiceResultSet {
        TODO()
    }
}