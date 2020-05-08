package micronaut.bookman.domain.book

interface BookRepository {
    fun get(id: String): Book
    fun delete(id: String)
    fun post(book: Book)
    fun put(book: Book)
}