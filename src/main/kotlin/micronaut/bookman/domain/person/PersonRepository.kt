package micronaut.bookman.domain.person

interface PersonRepository {
    fun get(id: String): Person
    fun post(person: Person)
    fun put(person: Person)
    fun delete(id: String)
}