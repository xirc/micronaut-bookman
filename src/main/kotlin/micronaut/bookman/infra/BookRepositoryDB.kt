package micronaut.bookman.infra

import io.micronaut.context.annotation.Primary
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.jdbc.runtime.JdbcOperations
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.CrudRepository
import micronaut.bookman.domain.book.Book
import micronaut.bookman.domain.book.BookRepository
import micronaut.bookman.domain.book.error.NoBookException
import java.util.*
import javax.inject.Singleton
import javax.transaction.Transactional

@Singleton
@Primary
@JdbcRepository(dialect = Dialect.MYSQL)
abstract class BookRepositoryDB(private val operations: JdbcOperations)
    : CrudRepository<BookEntity, UUID>, BookRepository {

    @Transactional
    override fun get(id: UUID): Book {
        return findById(id).map { it.to() }.orElseThrow { NoBookException(id.toString()) }
    }

    @Transactional
    override fun save(book: Book): Book {
        return save(BookEntity.from(book)).to()
    }

    @Transactional
    override fun update(book: Book): Book {
        return update(BookEntity.from(book)).to()
    }

    @Transactional
    override fun delete(id: UUID) {
        val book = findById(id).orElseThrow { NoBookException(id.toString()) }
        delete(book)
    }

}