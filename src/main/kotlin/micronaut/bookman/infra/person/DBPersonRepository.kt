package micronaut.bookman.infra.person

import io.micronaut.context.annotation.Primary
import micronaut.bookman.domain.person.FullName
import micronaut.bookman.domain.person.Person
import micronaut.bookman.domain.person.PersonId
import micronaut.bookman.domain.person.PersonRepository
import micronaut.bookman.domain.person.exceptions.DuplicatePersonException
import micronaut.bookman.domain.person.exceptions.NoPersonException
import micronaut.bookman.exceptions.AppIllegalArgumentException
import micronaut.bookman.infra.DatabaseTrait
import micronaut.bookman.infra.schema.PersonTable
import micronaut.bookman.infra.exceptions.IllegalDatabaseSchema
import micronaut.bookman.infra.exceptions.InfraException
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.SQLIntegrityConstraintViolationException
import javax.inject.Singleton
import javax.sql.DataSource

@Primary
@Singleton
class DBPersonRepository(
        private val source: DataSource,
        private val factory: Person.Factory
) : PersonRepository, DatabaseTrait {
    private fun createPerson(result: ResultRow): Person {
        return factory.createFromRepository(
                PersonId.fromString(result[PersonTable.id]),
                FullName(
                        result[PersonTable.firstName],
                        result[PersonTable.lastName]
                ),
                result[PersonTable.createdDate],
                result[PersonTable.updatedDate]
        )
    }

    override fun get(id: PersonId): Person {
        return withUtcZone {
            transaction(Database.connect(source)) {
                val person = PersonTable.select { PersonTable.id eq id.toString() }.singleOrNull()?.let {
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
                        it[id] = person.id.toString()
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
                val count = PersonTable.update({ PersonTable.id eq person.id.toString() }) {
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

    override fun delete(id: PersonId) {
        return transaction (Database.connect(source)) {
            val count = PersonTable.deleteWhere { PersonTable.id eq id.toString() }
            when (count) {
                0 -> throw NoPersonException(id)
                1 -> Unit
                else -> throw IllegalDatabaseSchema("Table ${PersonTable.tableName} has illegal schema.")
            }
        }
    }

    override fun getPage(page: Long): List<Person> {
        if (page < 0) throw AppIllegalArgumentException("page should be positive or zero.")
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
        if (offsetPage < 0) throw AppIllegalArgumentException("offsetPage should be positive or zero.")
        return withUtcZone {
            transaction(Database.connect(source)) {
                PersonTable.slice(PersonTable.id, PersonTable.updatedDate).selectAll()
                        .orderBy(PersonTable.updatedDate, SortOrder.DESC)
                        .limit(
                                PersonRepository.PageSize * PersonRepository.MaxPageCount,
                                PersonRepository.PageSize * offsetPage
                        ).count() / PersonRepository.PageSize
            }
        }
    }

    override fun getAll(ids: List<PersonId>): List<Person> {
        if (ids.isEmpty()) return emptyList()
        return withUtcZone {
            val sids = ids.map { it.toString() }
            transaction(Database.connect(source)) {
                PersonTable.selectAll().orWhere { PersonTable.id inList sids }.map {
                    createPerson(it)
                }
            }
        }
    }
}