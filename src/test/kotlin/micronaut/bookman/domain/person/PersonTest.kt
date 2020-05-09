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

    "Person can change his/her name" {
        val name = FullName("Harry", "Potter")
        val person = Person.create(name)
        val id = person.id
        person.name shouldBe name

        val newName = FullName("Ronald", "Weasley")
        person.updateName(newName)
        person.also {
            it.id shouldBe id
            it.name shouldBe newName
        }
    }

})