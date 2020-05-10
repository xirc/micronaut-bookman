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
import org.joda.time.DateTime
import java.lang.IllegalArgumentException
import java.sql.SQLIntegrityConstraintViolationException
import javax.inject.Singleton
import javax.sql.DataSource

@Primary
@Singleton
class DBBookRepository(
        private val source: DataSource,
        private val factory: Book.Factory
) : BookRepository, DBRepositoryTrait {
    data class BookValue(val id: String, val title: String, val createdDate: DateTime, val updatedDate: DateTime)
    data class BookAuthorValue(val bookId: String, val personId: String)
    private fun createBookValue(result: ResultRow): BookValue {
        return BookValue(
                result[BookTable.id],
                result[BookTable.title],
                result[BookTable.createdDate],
                result[BookTable.updatedDate]
        )
    }
    private fun createBookAuthorValue(result: ResultRow): BookAuthorValue {
        return BookAuthorValue(
                result[BookAuthorTable.book_id],
                result[BookAuthorTable.person_id]
        )
    }
    private fun createBook(book: BookValue, author: BookAuthorValue?): Book {
        return factory.createFromRepository(
                book.id,
                book.title,
                book.createdDate,
                book.updatedDate,
                author?.let { BookAuthor(author.personId) }
        )
    }

    override fun get(id: String): Book {
        return transaction (Database.connect(source)) {
            withUtcZone {
                val bookValue = BookTable
                        .select { BookTable.id eq id }.singleOrNull()?.let {
                            createBookValue(it)
                        }
                        ?: throw NoBookException(id)
                val bookAuthorValue = BookAuthorTable
                        .select { BookAuthorTable.book_id eq id }.singleOrNull()?.let {
                            createBookAuthorValue(it)
                        }
                createBook(bookValue, bookAuthorValue)
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

    override fun getPage(page: Long): List<Book> {
        if (page < 0) throw IllegalArgumentException("page should be positive or zero.")
        return transaction(Database.connect(source)) {
            // Left Join でもいいが、author を複数名にする可能性があるのでこのままにする
            val bookValues = BookTable.selectAll().orderBy(BookTable.updatedDate, SortOrder.DESC)
                    .limit(BookRepository.PageSize, BookRepository.PageSize * page)
                    .map {
                        createBookValue(it)
                    }
            val authorValues = BookAuthorTable.selectAll()
                    .orWhere { BookAuthorTable.book_id inList bookValues.map { it.id } }
                    .map {
                        createBookAuthorValue(it)
                    }
            val authorByBookId = authorValues.associateBy { it.bookId }
            bookValues.map {
                createBook(it, authorByBookId[it.id])
            }
        }
    }

    override fun countPage(offsetPage: Long): Long {
        if (offsetPage < 0) throw IllegalArgumentException("offsetPage should be positive or zero.")
        return transaction(Database.connect(source)) {
            BookTable.selectAll().orderBy(BookTable.updatedDate, SortOrder.DESC).limit(
                    BookRepository.PageSize * BookRepository.MaxPageCount,
                    BookRepository.PageSize * offsetPage
            ).count() / BookRepository.PageSize
        }
    }
}