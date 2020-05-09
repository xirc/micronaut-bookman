package micronaut.bookman.infra

import io.micronaut.context.annotation.Primary
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.jdbc.runtime.JdbcOperations
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.CrudRepository
import micronaut.bookman.domain.person.Person
import micronaut.bookman.domain.person.PersonRepository
import micronaut.bookman.domain.person.error.NoPersonException
import java.util.*
import javax.inject.Singleton
import javax.transaction.Transactional

@Singleton
@Primary
@JdbcRepository(dialect = Dialect.MYSQL)
abstract class PersonRepositoryDB(private val operations: JdbcOperations)
    : CrudRepository<PersonEntity, UUID>, PersonRepository {

    @Transactional
    override fun get(id: UUID): Person {
        return findById(id).map { it.to() }.orElseThrow { NoPersonException(id.toString()) }
    }

    @Transactional
    override fun save(person: Person): Person {
        return save(PersonEntity.from(person)).to()
    }

    @Transactional
    override fun update(person: Person): Person {
        return update(PersonEntity.from(person)).to()
    }

    @Transactional
    override fun delete(id: UUID) {
        val person = findById(id).orElseThrow { NoPersonException(id.toString()) }
        delete(person)
    }

}