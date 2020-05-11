package micronaut.bookman.query

data class BookSearchQueryServiceResultSet(
        val results: List<BookSearchQueryServiceResult>,
        val pageCount: Long
)