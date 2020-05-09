package micronaut.bookman.infra.schema

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.jodatime.datetime

object BookTable : Table() {
    // TODO collate を指定したほうが良いが、H2が対応していないのでテストが面倒になる
    override val tableName = "book"
    val id = varchar("id", 36)
    val title = varchar("title", 512)
    val createdDate = datetime("created_date")
    val updatedDate = datetime("updated_date")
    override val primaryKey = PrimaryKey(id)
}