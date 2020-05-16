package micronaut.bookman.domain.book

interface BookRepository {
    fun get(id: BookId): Book
    fun save(book: Book): Book
    fun update(book: Book): Book
    fun delete(id: BookId)

    fun getPage(page: Long): List<Book>
    fun countPage(offsetPage: Long): Long

    companion object {
        const val PageSize = 25
        const val MaxPageCount = 10
    }
}