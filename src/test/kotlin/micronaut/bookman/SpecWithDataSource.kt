package micronaut.bookman

import io.kotlintest.specs.AbstractStringSpec
import io.kotlintest.specs.StringSpec
import javax.sql.DataSource

open abstract class SpecWithDataSource(private val source: DataSource, body: AbstractStringSpec.() -> Unit): StringSpec(body) {
    override fun listeners() = listOf(DataSourceCleaner(source))
}