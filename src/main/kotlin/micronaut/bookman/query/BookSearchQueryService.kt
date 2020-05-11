package micronaut.bookman.query

interface BookSearchQueryService {
    fun searchAll(query: String, page: Long): BookSearchQueryResultSet

    companion object {
        const val PageSize = 25
        const val MaxPageCount = 10
    }
}