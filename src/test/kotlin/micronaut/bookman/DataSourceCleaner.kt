package micronaut.bookman

import io.kotlintest.TestCase
import io.kotlintest.extensions.TestListener
import micronaut.bookman.infra.schema.BookAuthorTable
import micronaut.bookman.infra.schema.BookTable
import micronaut.bookman.infra.schema.PersonTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import javax.sql.DataSource

class DataSourceCleaner(private val source: DataSource) : TestListener {
    override fun beforeTest(testCase: TestCase) {
        transaction(Database.connect(source)) {
            BookAuthorTable.deleteAll()
            BookTable.deleteAll()
            PersonTable.deleteAll()
        }
    }
}