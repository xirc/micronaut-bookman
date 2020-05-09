package micronaut.bookman.domain.person

import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.StringSpec
import io.micronaut.test.annotation.MicronautTest

@MicronautTest
class PersonTest : StringSpec({
    "Person has his/her name" {
        val name = FullName("Harry", "Potter")
        val person = Person.create(name)
        person.name shouldBe name
    }

    "Different persons can have same name." {
        val name = FullName("Harry", "Potter")
        val person1 = Person.create(name)
        val person2 = Person.create(name)
        person1.name shouldBe person2.name
        person1.id shouldNotBe person2.id
    }

    "Person can change his/her first name" {
        val name = FullName("Harry", "Potter")
        val person = Person.create(name)

        val newFirstName = "Ronald"
        person.updateFirstName(newFirstName)
        person.also {
            it.name.firstName shouldBe newFirstName
            it.name.lastName shouldBe name.lastName
        }
    }

    "Person can change his/her last name" {
        val name = FullName("Harry", "Potter")
        val person = Person.create(name)

        val newLastName = "Weasley"
        person.updateLastName(newLastName)
        person.also {
            it.name.firstName shouldBe name.firstName
            it.name.lastName shouldBe newLastName
        }
    }

})