package micronaut.bookman.domain.book

interface BookRepository {
    fun get(id: String): Book
    fun save(book: Book): Book
    fun update(book: Book): Book
    fun delete(id: String)
}