package micronaut.bookman.domain.person

interface PersonRepository {
    fun get(id: String): Person
    fun save(person: Person): Person
    fun update(person: Person): Person
    fun delete(id: String)

    fun getPage(page: Long): List<Person>
    fun countPage(offsetPage: Long): Long
    fun getAll(ids: List<String>): List<Person>

    companion object {
        const val PageSize = 25
        const val MaxPageCount = 10
    }
}