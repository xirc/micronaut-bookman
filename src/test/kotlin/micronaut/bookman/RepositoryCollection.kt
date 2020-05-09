package micronaut.bookman

import micronaut.bookman.infra.BookRepositoryDB
import micronaut.bookman.infra.PersonRepositoryDB
import javax.inject.Inject

class RepositoryCollection {
    @Inject
    lateinit var bookRepository: BookRepositoryDB
    @Inject
    lateinit var personRepository: PersonRepositoryDB
}