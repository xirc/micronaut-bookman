package micronaut.bookman.infra.book

import io.micronaut.context.annotation.Primary
import micronaut.bookman.domain.book.Book
import micronaut.bookman.domain.book.BookAuthor
import micronaut.bookman.domain.book.BookId
import micronaut.bookman.domain.book.BookRepository
import micronaut.bookman.domain.book.exceptions.DuplicateBookException
import micronaut.bookman.domain.book.exceptions.NoBookException
import micronaut.bookman.domain.person.PersonId
import micronaut.bookman.domain.person.exceptions.NoPersonException
import micronaut.bookman.exceptions.AppIllegalArgumentException
import micronaut.bookman.infra.schema.BookAuthorTable
import micronaut.bookman.infra.schema.BookTable
import micronaut.bookman.infra.DatabaseTrait
import micronaut.bookman.infra.exceptions.IllegalDatabaseSchema
import micronaut.bookman.infra.exceptions.InfraException
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import java.sql.BatchUpdateException
import java.sql.SQLIntegrityConstraintViolationException
import javax.inject.Singleton
import javax.sql.DataSource

@Primary
@Singleton
class DBBookRepository(
        private val source: DataSource,
        private val factory: Book.Factory
) : BookRepository, DatabaseTrait {

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
    private fun createBook(book: BookValue, authors: List<BookAuthorValue>): Book {
        return factory.createFromRepository(
                BookId.fromString(book.id),
                book.title,
                book.createdDate,
                book.updatedDate,
                authors.map {
                    BookAuthor(PersonId.fromString(it.personId))
                }
        )
    }

    private fun batchInsertBookAuthor(bookId: BookId, authors: List<BookAuthor>) {
        try {
            val sBookId = bookId.toString()
            BookAuthorTable.deleteWhere { BookAuthorTable.book_id eq sBookId }
            BookAuthorTable.batchInsert(authors) {
                this[BookAuthorTable.book_id] = sBookId
                this[BookAuthorTable.person_id] = it.personId.toString()
            }
        } catch (e: ExposedSQLException) {
            if (e.cause is BatchUpdateException) {
                // NOTE: 一意に確定させた方がよい？
                throw NoPersonException(authors.map { it.personId })
            } else {
                throw InfraException(e)
            }
        }
    }

    override fun get(id: BookId): Book {
        return withUtcZone {
            transaction(Database.connect(source)) {
                val sid = id.toString()
                val bookValue = BookTable
                        .select { BookTable.id eq sid }.singleOrNull()?.let {
                            createBookValue(it)
                        }
                        ?: throw NoBookException(id)
                val bookAuthorValues = BookAuthorTable
                        .select { BookAuthorTable.book_id eq sid }.map {
                            createBookAuthorValue(it)
                        }
                createBook(bookValue, bookAuthorValues)
            }
        }
    }

    override fun save(book: Book): Book {
        return withUtcZone {
            transaction(Database.connect(source)) {
                try {
                    BookTable.insert {
                        it[id] = book.id.toString()
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
                batchInsertBookAuthor(book.id, book.authors)
                book
            }
        }
    }

    override fun update(book: Book): Book {
        return withUtcZone {
            val sBookId = book.id.toString()
            transaction(Database.connect(source)) {
                val count = BookTable.update({ BookTable.id eq sBookId }) {
                    it[title] = book.title
                    it[createdDate] = book.createdDate
                    it[updatedDate] = book.updatedDate
                }
                when (count) {
                    0 -> throw NoBookException(book.id)
                    1 -> Unit
                    else -> throw IllegalDatabaseSchema("Table ${BookTable.tableName} has illegal schema.")
                }
                batchInsertBookAuthor(book.id, book.authors)
                book
            }
        }
    }

    override fun delete(id: BookId): Unit {
        val sid = id.toString()
        return transaction (Database.connect(source)) {
            BookAuthorTable.deleteWhere { BookAuthorTable.book_id eq sid }
            val count = BookTable.deleteWhere { BookTable.id eq sid }
            when (count) {
                0 -> throw NoBookException(id)
                1 -> Unit
                else -> throw IllegalDatabaseSchema("Table ${BookTable.tableName} has illegal schema.")
            }
        }
    }

    override fun getPage(page: Long): List<Book> {
        if (page < 0) throw AppIllegalArgumentException("page should be positive or zero.")
        return withUtcZone {
            transaction(Database.connect(source)) {
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
                // Multi-Set がなさそう
                var authorByBookId = mutableMapOf<String, MutableList<BookAuthorValue>>()
                for (authorValue in authorValues) {
                    authorByBookId.getOrPut(authorValue.bookId, { mutableListOf() })
                    authorByBookId[authorValue.bookId]?.add(authorValue)
                }

                bookValues.map {
                    createBook(it, authorByBookId[it.id] ?: emptyList())
                }
            }
        }
    }

    override fun countPage(offsetPage: Long): Long {
        if (offsetPage < 0) throw AppIllegalArgumentException("offsetPage should be positive or zero.")
        return withUtcZone {
            transaction(Database.connect(source)) {
                BookTable.slice(BookTable.id, BookTable.updatedDate).selectAll()
                        .orderBy(BookTable.updatedDate, SortOrder.DESC)
                        .limit(
                                BookRepository.PageSize * BookRepository.MaxPageCount,
                                BookRepository.PageSize * offsetPage
                        ).count() / BookRepository.PageSize
            }
        }
    }
}