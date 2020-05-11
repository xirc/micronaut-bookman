package micronaut.bookman.infra

import io.kotlintest.matchers.collections.shouldBeOneOf
import io.kotlintest.matchers.collections.shouldBeSortedWith
import io.kotlintest.matchers.collections.shouldContain
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotThrowAny
import io.kotlintest.shouldThrow
import io.micronaut.test.annotation.MicronautTest
import micronaut.bookman.SpecWithDataSource
import micronaut.bookman.domain.book.Book
import micronaut.bookman.domain.book.BookAuthor
import micronaut.bookman.domain.book.BookRepository
import micronaut.bookman.domain.book.exceptions.DuplicateBookException
import micronaut.bookman.domain.book.exceptions.NoBookException
import micronaut.bookman.domain.person.exceptions.NoPersonException
import micronaut.bookman.infra.book.DBBookRepository
import java.lang.IllegalArgumentException
import java.util.*
import javax.sql.DataSource

@MicronautTest
class DBBookRepositoryTest(
        private val source: DataSource,
        private val factory: Book.Factory,
        private val bookFixture: DBBookFixture,
        private val personFixture: DBPersonFixture
) : SpecWithDataSource(source, {
    val repository = DBBookRepository(source, factory)

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
        val book = bookFixture.create()
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
        val book = bookFixture.create()
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
        val book = bookFixture.create()
        val person = personFixture.create()
        val newTitle = "a new title"
        val authors = listOf(BookAuthor(person.id))

        book.updateTitle(newTitle)
        book.updateAuthors(authors)
        repository.update(book)

        val newBook = repository.get(book.id)
        newBook.title shouldBe newTitle
        newBook.authors.size shouldBe 1
        newBook.authors.map { it.personId } shouldContain person.id
    }

    "DBBookRepository can delete a author of book" {
        val book = bookFixture.create()
        val person = personFixture.create()
        book.updateAuthors(listOf(BookAuthor(person.id)))

        val bookWithAuthor = repository.update(book)
        bookWithAuthor.updateAuthors(emptyList())

        val bookWithoutAuthor = repository.update(bookWithAuthor)
        bookWithoutAuthor.authors.size shouldBe 0
    }

    "DBBookRepository cannot update a book with invalid ID" {
        val book = factory.create()
        shouldThrow<NoBookException> {
            repository.update(book)
        }

        repository.save(book)

        shouldThrow<NoPersonException> {
            val personId = UUID.randomUUID().toString()
            val authors = listOf(BookAuthor(personId))
            book.updateAuthors(authors)
            repository.update(book)
        }
    }

    "DBBookRepository can get a page" {
        bookFixture.createCollection(BookRepository.PageSize + 1)

        val personsInPage0 = repository.getPage(0)
        personsInPage0.size shouldBe BookRepository.PageSize
        personsInPage0 shouldBeSortedWith compareByDescending { it.updatedDate }

        val personsInPage1 = repository.getPage(1)
        personsInPage1.size shouldBe 1
        personsInPage1 shouldBeSortedWith compareByDescending { it.updatedDate }
    }

    "DBBookRepository can get a page that contains a book which have a author." {
        val origBooks = bookFixture.createCollection(5)
        val persons = personFixture.createCollection(3)
        origBooks[0].updateAuthors(listOf(BookAuthor(persons[0].id)))
        origBooks[2].updateAuthors(listOf(BookAuthor(persons[1].id)))
        origBooks[3].updateAuthors(listOf(BookAuthor(persons[1].id)))
        origBooks[4].updateAuthors(listOf(BookAuthor(persons[2].id)))
        for (book in origBooks) {
            repository.update(book)
        }
        val origBookById = origBooks.associateBy { it.id }

        val books = repository.getPage(0)
        books.size shouldBe 5
        books shouldBeSortedWith compareByDescending { it.updatedDate }
        for (book in books) {
            val origBook = origBookById[book.id]
            if (origBook == null) {
                book.authors.size shouldBe 0
            } else {
                val personIds = origBook.authors.map { it.personId }
                for (author in book.authors) {
                    author.personId shouldBeOneOf personIds
                }
            }
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
        bookFixture.createCollection(BookRepository.PageSize * (BookRepository.MaxPageCount + 1) + 1)
        repository.countPage(0) shouldBe BookRepository.MaxPageCount
        repository.countPage(1) shouldBe BookRepository.MaxPageCount
        repository.countPage(2) shouldBe BookRepository.MaxPageCount - 1
        repository.countPage(3) shouldBe BookRepository.MaxPageCount - 2
        repository.countPage(BookRepository.MaxPageCount.toLong()) shouldBe 1
        repository.countPage(BookRepository.MaxPageCount + 1L) shouldBe 0
    }

})