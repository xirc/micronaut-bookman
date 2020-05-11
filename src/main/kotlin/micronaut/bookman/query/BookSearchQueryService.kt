package micronaut.bookman.query

interface BookSearchQueryService {
    fun searchAll(q: String, page: Int = 0): BookSearchQueryServiceResultSet

    companion object {
        const val PageSize = 25
        const val MaxPage = 10
    }
}