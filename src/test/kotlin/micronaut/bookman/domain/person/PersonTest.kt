package micronaut.bookman.domain.person

import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.StringSpec
import io.micronaut.test.annotation.MicronautTest
import micronaut.bookman.domain.ConstantDateTimeFactory
import micronaut.bookman.domain.person.error.IllegalPersonStateException
import micronaut.bookman.domain.time.ServerDateTimeFactory
import org.joda.time.DateTime

@MicronautTest
class PersonTest : StringSpec({
    // NOTE: デフォルト
    val factory = Person.Factory(ServerDateTimeFactory())

    // NOTE: 時刻を厳密に指定したいところで使う
    // 誤って DateTime.now() が使われたときにわかるように1日程度巻き戻しておく
    val timeFactory = ConstantDateTimeFactory(DateTime.now().minusDays(1))
    val swFactory = Person.Factory(timeFactory)

    "Person has default name" {
        val person = factory.create()
        person.name shouldBe Person.DefaultName
    }

    "Person has created date that is exactly same provided by timeFactory" {
        val person = swFactory.create()
        person.createdDate shouldBe timeFactory.value
    }

    "Person has updated date that is exactly same provided by timeFactory" {
        val person = swFactory.create()
        person.updatedDate shouldBe timeFactory.value
    }

    "Person has updated date that should be exactly equal to created date as default" {
        val person = factory.create()
        person.updatedDate shouldBe person.createdDate
    }

    "Person can change his/her name" {
        val person = swFactory.create()

        // 更新日時が新しくなるように時間を進める
        timeFactory.value = timeFactory.value.plusMillis(1)

        val name = FullName("Harry", "Potter")
        person.updateName(name)
        person.also {
            it.name shouldBe name
            it.updatedDate shouldBe timeFactory.value
        }
    }
    "Person can change his/her first name" {
        val person = swFactory.create()

        // 更新日時が新しくなるように時間を進める
        timeFactory.value = timeFactory.value.plusMillis(1)

        val firstName = "Ronald"
        person.updateFirstName(firstName)
        person.also {
            it.name.firstName shouldBe firstName
            it.name.lastName shouldBe Person.DefaultName.lastName
            it.updatedDate shouldBe timeFactory.value
        }
    }

    "Person can change his/her last name" {
        val person = swFactory.create()

        // 更新日時が新しくなるように時間を進める
        timeFactory.value = timeFactory.value.plusMillis(1)

        val lastName = "Weasley"
        person.updateLastName(lastName)
        person.also {
            it.name.firstName shouldBe Person.DefaultName.lastName
            it.name.lastName shouldBe lastName
            it.updatedDate shouldBe timeFactory.value
        }
    }

    "Person cannot have updated date that is before created date" {
        val person = swFactory.create()
        // 更新に失敗するか確認するため時間を巻き戻す
        timeFactory.value = timeFactory.value.minusMillis(1)
        shouldThrow<IllegalPersonStateException> {
            person.updateFirstName("Ronald")
        }
        shouldThrow<IllegalPersonStateException> {
            person.updateFirstName("Weasley")
        }
    }

})