package micronaut.bookman

import io.kotlintest.TestCase
import io.kotlintest.extensions.TestListener

class DataSourceCleaner(private val repositoryCollection: RepositoryCollection) : TestListener {
    override fun beforeTest(testCase: TestCase) {
        repositoryCollection.bookRepository.deleteAll()
        repositoryCollection.personRepository.deleteAll()
    }
}