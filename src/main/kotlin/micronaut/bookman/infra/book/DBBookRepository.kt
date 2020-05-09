package micronaut.bookman.infra.book

import micronaut.bookman.domain.book.Book
import micronaut.bookman.domain.book.BookAuthor
import micronaut.bookman.domain.book.BookRepository
import micronaut.bookman.domain.book.error.NoBookException
import micronaut.bookman.infra.DBRepositoryTrait
import micronaut.bookman.infra.error.IllegalDatabaseSchema
import micronaut.bookman.infra.extension.insertOrUpdate
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import javax.sql.DataSource

class DBBookRepository(
        private val source: DataSource,
        private val factory: Book.Factory
) : BookRepository, DBRepositoryTrait {
    private fun createBookAuthor(result: ResultRow): BookAuthor {
        return BookAuthor(result[BookAuthorTable.person_id])
    }
    private fun createBook(result: ResultRow, author: BookAuthor?): Book {
        return factory.createFromRepository(
                result[BookTable.id],
                result[BookTable.title],
                result[BookTable.createdDate],
                result[BookTable.updatedDate],
                author
        )
    }

    override fun get(id: String): Book {
        return transaction (Database.connect(source)) {
            catchingKnownException {
                withUtcZone {
                    val bookAuthor = BookAuthorTable.select { BookAuthorTable.book_id eq id }.singleOrNull()?.let {
                        createBookAuthor(it)
                    }
                    val book = BookTable.select { BookTable.id eq id }.singleOrNull()?.let {
                        createBook(it, bookAuthor)
                    }
                    book?.apply {  } ?: throw NoBookException(id)
                }
            }
        }
    }

    override fun post(book: Book) {
        return transaction (Database.connect(source)) {
            catchingKnownException {
                withUtcZone {
                    BookTable.insert {
                        it[id] = book.id
                        it[title] = book.title
                        it[createdDate] = book.createdDate
                        it[updatedDate] = book.updatedDate
                    }
                    book.author?.also { author: BookAuthor ->
                        BookAuthorTable.insert {
                            it[book_id] = book.id
                            it[person_id] = author.personId
                        }
                    }
                }
            }
        }
    }

    override fun put(book: Book) {
        return transaction (Database.connect(source)) {
            catchingKnownException {
                withUtcZone {
                    val count = BookTable.update({ BookTable.id eq book.id }) {
                        it[title] = book.title
                        it[createdDate] = book.createdDate
                        it[updatedDate] = book.updatedDate
                    }
                    when (count) {
                        0 -> throw NoBookException(book.id)
                        1 -> Unit
                        else -> throw IllegalDatabaseSchema("Table ${BookTable.tableName} has illegal schema.")
                    }
                    book.author?.let { author: BookAuthor ->
                        BookAuthorTable.insertOrUpdate(BookAuthorTable.book_id){
                            it[book_id] = book.id
                            it[person_id] = author.personId
                        }
                    }
                }
            }
        }
    }

    override fun delete(id: String) {
        return transaction (Database.connect(source)) {
            catchingKnownException {
                BookAuthorTable.deleteWhere { BookAuthorTable.book_id eq id }
                val count = BookTable.deleteWhere { BookTable.id eq id }
                when (count) {
                    0 -> throw NoBookException(id)
                    1 -> Unit
                    else -> throw IllegalDatabaseSchema("Table ${BookTable.tableName} has illegal schema.")
                }
            }
        }
    }
}