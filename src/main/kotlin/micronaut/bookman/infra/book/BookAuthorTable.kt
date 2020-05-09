package micronaut.bookman.infra.book

import micronaut.bookman.infra.person.PersonTable
import org.jetbrains.exposed.sql.Table

object BookAuthorTable : Table() {
    override val tableName = "book_author"
    val book_id = varchar("book_id", 36).references(BookTable.id)
    val person_id = varchar("person_id", 36).references(PersonTable.id)
    override val primaryKey = PrimaryKey(book_id)
}