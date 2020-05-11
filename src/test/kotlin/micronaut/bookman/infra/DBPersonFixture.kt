package micronaut.bookman.infra

import micronaut.bookman.domain.person.FullName
import micronaut.bookman.domain.person.Person
import micronaut.bookman.infra.person.DBPersonRepository
import java.util.*
import javax.inject.Singleton
import javax.sql.DataSource

@Singleton
class DBPersonFixture
(
        private val source: DataSource,
        private val personFactory: Person.Factory
) {
    private val personRepository = DBPersonRepository(source, personFactory)

    fun create(): Person {
        val q = UUID.randomUUID().toString()
        val person = personFactory.create()
        person.updateName(FullName("f$q", "l$q"))
        return personRepository.save(person)
    }
    fun createCollection(n: Int): List<Person> {
        // TODO Batch Insert する
        var persons = mutableListOf<Person>()
        for (i in 0 until n ) {
            val q = UUID.randomUUID().toString()
            val person = personFactory.create()
            person.updateName(FullName("f$i$q", "l$i$q"))
            val savedPerson = personRepository.save(person)
            persons.add(savedPerson)
        }
        return persons
    }
}