package micronaut.bookman.infra

import io.kotlintest.matchers.collections.shouldBeSortedWith
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotThrowAny
import io.kotlintest.shouldThrow
import io.micronaut.test.annotation.MicronautTest
import micronaut.bookman.SpecWithDataSource
import micronaut.bookman.domain.book.Book
import micronaut.bookman.domain.book.BookAuthor
import micronaut.bookman.domain.book.BookRepository
import micronaut.bookman.domain.book.error.DuplicateBookException
import micronaut.bookman.domain.book.error.NoBookException
import micronaut.bookman.domain.person.FullName
import micronaut.bookman.domain.person.Person
import micronaut.bookman.domain.person.error.NoPersonException
import micronaut.bookman.infra.book.DBBookRepository
import micronaut.bookman.infra.person.DBPersonRepository
import java.lang.IllegalArgumentException
import java.util.*
import javax.sql.DataSource

@MicronautTest
class DBBookRepositoryTest(
        private val source: DataSource,
        private val factory: Book.Factory,
        private val personFactory: Person.Factory
) : SpecWithDataSource(source, {
    val repository = DBBookRepository(source, factory)
    val personRepository = DBPersonRepository(source, personFactory)

    fun createFixture(): Book {
        val book = factory.create()
        repository.save(book)
        return book
    }
    fun createFixtures(n: Int): List<Book> {
        var books = mutableListOf<Book>()
        for (i in 0 until n) {
            val book = factory.create()
            book.updateTitle("book $i")
            val savedBook = repository.save(book)
            books.add(savedBook)
        }
        return books
    }
    fun createPersonFixtures(n: Int): List<Person> {
        var persons = mutableListOf<Person>()
        for (i in 0 until n ) {
            val person = personFactory.create(FullName("First$i", "Last$i"))
            val savedPerson = personRepository.save(person)
            persons.add(savedPerson)
        }
        return persons
    }

    "DBBookRepository can create a book" {
        val book = factory.create()
        repository.save(book)
    }

    "DBBookRepository cannot create a post twice" {
        val book = factory.create()
        shouldNotThrowAny {
            repository.save(book)
        }
        shouldThrow<DuplicateBookException> {
            repository.save(book)
        }
    }

    "DBBookRepository can get a book" {
        val book = createFixture()
        val referenceBook = repository.get(book.id)
        referenceBook.run {
            id shouldBe book.id
            title shouldBe book.title
            createdDate shouldBe book.createdDate
            updatedDate shouldBe book.updatedDate
        }
    }

    "DBBookRepository cannot get a book with invalid ID" {
        val id = UUID.randomUUID().toString()
        shouldThrow<NoBookException> {
            repository.get(id)
        }
    }

    "DBBookRepository can delete a book" {
        val book = createFixture()
        repository.delete(book.id)
        shouldThrow<NoBookException> {
            repository.get(book.id)
        }
    }

    "DBBookRepository cannot delete a book with invalid ID" {
        val book = factory.create()
        shouldThrow<NoBookException> {
            repository.get(book.id)
        }
        shouldThrow<NoBookException> {
            repository.delete(book.id)
        }
    }

    "DBBookRepository can update a book" {
        val book = factory.create()
        repository.save(book)
        val person = personFactory.create(FullName("Harry", "Potter"))
        personRepository.save(person)
        val newTitle = "a new title"

        book.updateTitle(newTitle)
        book.updateAuthor(BookAuthor(person.id))
        repository.update(book)

        val newBook = repository.get(book.id)
        newBook.title shouldBe newTitle
        newBook.author?.personId shouldBe person.id
    }

    "DBBookRepository cannot update a book with invalid ID" {
        val book = factory.create()
        shouldThrow<NoBookException> {
            repository.update(book)
        }

        repository.save(book)

        shouldThrow<NoPersonException> {
            val personId = UUID.randomUUID().toString()
            book.updateAuthor(BookAuthor(personId))
            repository.update(book)
        }
    }

    "DBBookRepository can get a page" {
        createFixtures(BookRepository.PageSize + 1)

        val personsInPage0 = repository.getPage(0)
        personsInPage0.size shouldBe BookRepository.PageSize
        personsInPage0 shouldBeSortedWith compareByDescending { it.updatedDate }

        val personsInPage1 = repository.getPage(1)
        personsInPage1.size shouldBe 1
        personsInPage1 shouldBeSortedWith compareByDescending { it.updatedDate }
    }

    "DBBookRepository can get a page that contains a book which have a author." {
        val origBooks = createFixtures(5)
        val persons = createPersonFixtures(3)
        origBooks[0].updateAuthor(BookAuthor(persons[0].id))
        origBooks[2].updateAuthor(BookAuthor(persons[1].id))
        origBooks[3].updateAuthor(BookAuthor(persons[1].id))
        origBooks[4].updateAuthor(BookAuthor(persons[2].id))
        for (book in origBooks) {
            repository.update(book)
        }
        val origBookById = origBooks.associateBy { it.id }

        val books = repository.getPage(0)
        books.size shouldBe 5
        books shouldBeSortedWith compareByDescending { it.updatedDate }
        for (book in books) {
            book.author?.personId shouldBe origBookById[book.id]?.author?.personId
        }
    }

    "DBBookRepository cannot get a page with invalid page number." {
        shouldThrow<IllegalArgumentException> {
            repository.getPage(-1)
        }
    }

    "DBBookRepository can get empty page" {
        repository.getPage(0).size shouldBe 0
    }

    "DBBookRepository can get page count" {
        // The repository has (MaxPageCount + 2) pages.
        createFixtures(BookRepository.PageSize * (BookRepository.MaxPageCount + 1) + 1)
        repository.countPage(0) shouldBe BookRepository.MaxPageCount
        repository.countPage(1) shouldBe BookRepository.MaxPageCount
        repository.countPage(2) shouldBe BookRepository.MaxPageCount - 1
        repository.countPage(3) shouldBe BookRepository.MaxPageCount - 2
        repository.countPage(BookRepository.MaxPageCount.toLong()) shouldBe 1
        repository.countPage(BookRepository.MaxPageCount + 1L) shouldBe 0
    }

})