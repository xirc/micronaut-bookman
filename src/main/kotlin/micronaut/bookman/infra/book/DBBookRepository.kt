package micronaut.bookman.infra.book

import micronaut.bookman.domain.book.Book
import micronaut.bookman.domain.book.BookRepository
import micronaut.bookman.domain.book.error.NoBookException
import micronaut.bookman.infra.DBRepositoryTrait
import micronaut.bookman.infra.error.IllegalDatabaseSchema
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import javax.sql.DataSource

class DBBookRepository(
        private val source: DataSource,
        private val factory: Book.Factory
) : BookRepository, DBRepositoryTrait {
    private fun createBook(result: ResultRow): Book {
        return factory.createFromRepository(
                result[BookTable.id],
                result[BookTable.title],
                result[BookTable.createdDate],
                result[BookTable.updatedDate]
        )
    }

    override fun get(id: String): Book {
        return transaction (Database.connect(source)) {
            catchingKnownException {
                withUtcZone {
                    val book = BookTable.select { BookTable.id eq id }.singleOrNull()?.let {
                        createBook(it)
                    }
                    book ?: throw NoBookException(id)
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
                }
            }
        }
    }

    override fun delete(id: String) {
        return transaction (Database.connect(source)) {
            catchingKnownException {
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