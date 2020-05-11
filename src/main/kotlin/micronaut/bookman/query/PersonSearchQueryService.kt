package micronaut.bookman.query

interface PersonSearchQueryService {
    fun searchAll(query: String, page: Long): PersonSearchQueryResultSet

    companion object {
        const val PageSize = 25
        const val MaxPageCount = 10
    }
}