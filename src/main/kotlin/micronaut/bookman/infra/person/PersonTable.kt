package micronaut.bookman.infra.person

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.jodatime.datetime

object PersonTable : Table() {
    // TODO collate を指定したほうが良いが、H2が対応していないのでテストが面倒になる
    override val tableName = "person"
    val id = varchar("id", 36)
    val firstName = varchar("first_name", 128)
    val lastName = varchar("last_name", 128)
    val createdDate = datetime("created_date")
    val updatedDate = datetime("updated_date")
    override val primaryKey = PrimaryKey(id)
}