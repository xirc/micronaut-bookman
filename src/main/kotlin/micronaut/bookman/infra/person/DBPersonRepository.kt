package micronaut.bookman.infra.person

import micronaut.bookman.domain.person.FullName
import micronaut.bookman.domain.person.Person
import micronaut.bookman.domain.person.PersonRepository
import micronaut.bookman.domain.person.error.DuplicatePersonException
import micronaut.bookman.domain.person.error.NoPersonException
import micronaut.bookman.infra.DBRepositoryTrait
import micronaut.bookman.infra.error.IllegalDatabaseSchema
import micronaut.bookman.infra.error.InfraException
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.SQLIntegrityConstraintViolationException
import javax.sql.DataSource


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
        return transaction(Database.connect(source)) {
            withUtcZone {
                val person = PersonTable.select { PersonTable.id eq id }.singleOrNull()?.let {
                    createPerson(it)
                }
                person ?: throw NoPersonException(id)
            }
        }
    }

    override fun save(person: Person): Person {
        return transaction (Database.connect(source)) {
            withUtcZone {
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
        return transaction (Database.connect(source)) {
            withUtcZone {
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
}