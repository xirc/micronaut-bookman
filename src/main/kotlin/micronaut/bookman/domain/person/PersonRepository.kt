package micronaut.bookman.domain.person

import java.util.*

interface PersonRepository {
    fun get(id: UUID): Person
    fun save(person: Person): Person
    fun update(person: Person): Person
    fun delete(id: UUID)
}