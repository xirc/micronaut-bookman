package micronaut.bookman.infra.person

import micronaut.bookman.domain.person.FullName
import micronaut.bookman.domain.person.Person
import micronaut.bookman.domain.person.PersonRepository
import micronaut.bookman.domain.person.error.NoPersonException
import micronaut.bookman.infra.DBRepositoryTrait
import micronaut.bookman.infra.error.IllegalDatabaseSchema
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
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
            catchingKnownException {
                withUtcZone {
                    val person = PersonTable.select { PersonTable.id eq id }.singleOrNull()?.let {
                        createPerson(it)
                    }
                    person ?: throw NoPersonException(id)
                }
            }
        }
    }

    override fun post(person: Person) {
        return transaction (Database.connect(source)) {
            catchingKnownException {
                withUtcZone {
                    PersonTable.insert {
                        it[id] = person.id
                        it[firstName] = person.name.firstName
                        it[lastName] = person.name.lastName
                        it[createdDate] = person.createdDate
                        it[updatedDate] = person.updatedDate
                    }
                }
            }
        }
    }

    override fun put(person: Person) {
        return transaction (Database.connect(source)) {
            catchingKnownException {
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
                }
            }
        }
    }

    override fun delete(id: String) {
        return transaction (Database.connect(source)) {
            catchingKnownException {
                val count = PersonTable.deleteWhere { PersonTable.id eq id }
                when (count) {
                    0 -> throw NoPersonException(id)
                    1 -> Unit
                    else -> throw IllegalDatabaseSchema("Table ${PersonTable.tableName} has illegal schema.")
                }
            }
        }
    }
}