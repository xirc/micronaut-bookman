package micronaut.bookman

import io.kotlintest.specs.AbstractStringSpec
import io.kotlintest.specs.StringSpec

abstract class SpecWithDataSource(private val repositoryCollection: RepositoryCollection, body: AbstractStringSpec.() -> Unit): StringSpec(body) {
    override fun listeners() = listOf(DataSourceCleaner(repositoryCollection))
}