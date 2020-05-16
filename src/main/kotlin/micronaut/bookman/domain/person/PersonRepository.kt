package micronaut.bookman.domain.person

interface PersonRepository {
    fun get(id: PersonId): Person
    fun save(person: Person): Person
    fun update(person: Person): Person
    fun delete(id: PersonId)

    fun getPage(page: Long): List<Person>
    fun countPage(offsetPage: Long): Long
    fun getAll(ids: List<PersonId>): List<Person>

    companion object {
        const val PageSize = 25
        const val MaxPageCount = 10
    }
}