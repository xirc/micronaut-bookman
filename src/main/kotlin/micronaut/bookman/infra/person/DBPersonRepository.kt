package micronaut.bookman.infra.person

import io.micronaut.context.annotation.Primary
import micronaut.bookman.domain.person.FullName
import micronaut.bookman.domain.person.Person
import micronaut.bookman.domain.person.PersonRepository
import micronaut.bookman.domain.person.error.DuplicatePersonException
import micronaut.bookman.domain.person.error.NoPersonException
import micronaut.bookman.infra.DBRepositoryTrait
import micronaut.bookman.infra.schema.PersonTable
import micronaut.bookman.infra.error.IllegalDatabaseSchema
import micronaut.bookman.infra.error.InfraException
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.lang.IllegalArgumentException
import java.sql.SQLIntegrityConstraintViolationException
import javax.inject.Singleton
import javax.sql.DataSource

@Primary
@Singleton
class DBPersonRepository(
        private val source: DataSource,
        private val factory: Person.Factory
) : PersonRepository, DBRepositoryTrait {
    private fun createPerson(result: ResultRow): Person {
        return factory.createFromRepository(
                result[PersonTable.id],
                FullName(
                        result[PersonTable.firstName],
                        result[PersonTable.lastName]
                ),
                result[PersonTable.createdDate],
                result[PersonTable.updatedDate]
        )
    }

    override fun get(id: String): Person {
        return withUtcZone {
            transaction(Database.connect(source)) {
                val person = PersonTable.select { PersonTable.id eq id }.singleOrNull()?.let {
                    createPerson(it)
                }
                person ?: throw NoPersonException(id)
            }
        }
    }

    override fun save(person: Person): Person {
        return withUtcZone {
            transaction(Database.connect(source)) {
                try {
                    PersonTable.insert {
                        it[id] = person.id
                        it[firstName] = person.name.firstName
                        it[lastName] = person.name.lastName
                        it[createdDate] = person.createdDate
                        it[updatedDate] = person.updatedDate
                    }
                } catch (e: ExposedSQLException) {
                    if (e.cause is SQLIntegrityConstraintViolationException) {
                        throw DuplicatePersonException(person.id)
                    } else {
                        throw InfraException(e)
                    }
                }
                person
            }
        }
    }

    override fun update(person: Person): Person {
        return withUtcZone {
            transaction(Database.connect(source)) {
                val count = PersonTable.update({ PersonTable.id eq person.id }) {
                    it[firstName] = person.name.firstName
                    it[lastName] = person.name.lastName
                    it[createdDate] = person.createdDate
                    it[updatedDate] = person.updatedDate
                }
                when (count) {
                    0 -> throw NoPersonException(person.id)
                    1 -> Unit
                    else -> throw IllegalDatabaseSchema("Table ${PersonTable.tableName} has illegal schema.")
                }
                person
            }
        }
    }

    override fun delete(id: String) {
        return transaction (Database.connect(source)) {
            val count = PersonTable.deleteWhere { PersonTable.id eq id }
            when (count) {
                0 -> throw NoPersonException(id)
                1 -> Unit
                else -> throw IllegalDatabaseSchema("Table ${PersonTable.tableName} has illegal schema.")
            }
        }
    }

    override fun getPage(page: Long): List<Person> {
        if (page < 0) throw IllegalArgumentException("page should be positive or zero.")
        return withUtcZone {
            transaction(Database.connect(source)) {
                PersonTable.selectAll().orderBy(PersonTable.updatedDate, SortOrder.DESC).limit(
                        PersonRepository.PageSize, PersonRepository.PageSize * page
                ).map {
                    createPerson(it)
                }
            }
        }
    }

    override fun countPage(offsetPage: Long): Long {
        if (offsetPage < 0) throw IllegalArgumentException("offsetPage should be positive or zero.")
        return withUtcZone {
            transaction(Database.connect(source)) {
                PersonTable.selectAll().orderBy(PersonTable.updatedDate, SortOrder.DESC).limit(
                        PersonRepository.PageSize * PersonRepository.MaxPageCount,
                        PersonRepository.PageSize * offsetPage
                ).count() / PersonRepository.PageSize
            }
        }
    }

    override fun getAll(ids: List<String>): List<Person> {
        if (ids.isEmpty()) return emptyList()
        return withUtcZone {
            transaction(Database.connect(source)) {
                PersonTable.selectAll().orWhere { PersonTable.id inList ids }.map {
                    createPerson(it)
                }
            }
        }
    }
}