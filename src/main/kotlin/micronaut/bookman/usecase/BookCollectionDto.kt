package micronaut.bookman.usecase

data class BookCollectionDto(
        val books: List<BookDto>,
        val pageCount: Long
)