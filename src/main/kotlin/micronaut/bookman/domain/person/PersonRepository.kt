package micronaut.bookman.domain.person

interface PersonRepository {
    fun get(id: String): Person
    fun save(person: Person): Person
    fun update(person: Person): Person
    fun delete(id: String)
}