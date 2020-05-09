package micronaut.bookman.infra.book

import io.micronaut.context.annotation.Primary
import micronaut.bookman.domain.book.Book
import micronaut.bookman.domain.book.BookAuthor
import micronaut.bookman.domain.book.BookRepository
import micronaut.bookman.domain.book.error.DuplicateBookException
import micronaut.bookman.domain.book.error.NoBookException
import micronaut.bookman.domain.person.error.NoPersonException
import micronaut.bookman.infra.schema.BookAuthorTable
import micronaut.bookman.infra.schema.BookTable
import micronaut.bookman.infra.DBRepositoryTrait
import micronaut.bookman.infra.error.IllegalDatabaseSchema
import micronaut.bookman.infra.error.InfraException
import micronaut.bookman.infra.extension.insertOrUpdate
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.SQLIntegrityConstraintViolationException
import javax.inject.Singleton
import javax.sql.DataSource

@Primary
@Singleton
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
            withUtcZone {
                val bookAuthor = BookAuthorTable.select { BookAuthorTable.book_id eq id }.singleOrNull()?.let {
                    createBookAuthor(it)
                }
                val book = BookTable.select { BookTable.id eq id }.singleOrNull()?.let {
                    createBook(it, bookAuthor)
                }
                book ?: throw NoBookException(id)
            }
        }
    }

    override fun save(book: Book): Book {
        return transaction (Database.connect(source)) {
            withUtcZone {
                try {
                    BookTable.insert {
                        it[id] = book.id
                        it[title] = book.title
                        it[createdDate] = book.createdDate
                        it[updatedDate] = book.updatedDate
                    }
                } catch (e: ExposedSQLException) {
                    if (e.cause is SQLIntegrityConstraintViolationException) {
                        throw DuplicateBookException(book.id)
                    } else {
                        throw InfraException(e)
                    }
                }
                book.author?.also { author: BookAuthor ->
                    try {
                        BookAuthorTable.insert {
                            it[book_id] = book.id
                            it[person_id] = author.personId
                        }
                    } catch (e: ExposedSQLException) {
                        if (e.cause is SQLIntegrityConstraintViolationException) {
                            throw NoPersonException(author.personId)
                        } else {
                            throw InfraException(e)
                        }
                    }
                }
                book
            }
        }
    }

    override fun update(book: Book): Book {
        return transaction (Database.connect(source)) {
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
                    try {
                        BookAuthorTable.insertOrUpdate(BookAuthorTable.book_id) {
                            it[book_id] = book.id
                            it[person_id] = author.personId
                        }
                    } catch (e: ExposedSQLException) {
                        if (e.cause is SQLIntegrityConstraintViolationException) {
                            throw NoPersonException(author.personId)
                        } else {
                            throw InfraException(e)
                        }
                    }
                }
                book
            }
        }
    }

    override fun delete(id: String): Unit {
        return transaction (Database.connect(source)) {
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