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
import java.util.*

@MicronautTest
class PersonTest : StringSpec({
    // NOTE: デフォルト
    val factory = Person.Factory(ServerDateTimeFactory())

    // NOTE: 時刻を厳密に指定したいところで使う
    // 誤って DateTime.now() が使われたときにわかるように1日程度巻き戻しておく
    val timeFactory = ConstantDateTimeFactory(DateTime.now().minusDays(1))
    val swFactory = Person.Factory(timeFactory)

    "Person has his/her name" {
        val name = FullName("Harry", "Potter")
        val person = factory.create(name)
        person.name shouldBe name
    }

    "Person has created date that is exactly same provided by timeFactory" {
        val person = swFactory.create(FullName("Harry", "Potter"))
        person.createdDate shouldBe timeFactory.value
    }

    "Person has updated date that is exactly same provided by timeFactory" {
        val person = swFactory.create(FullName("Harry", "Potter"))
        person.updatedDate shouldBe timeFactory.value
    }

    "Person has updated date that should be exactly equal to created date as default" {
        val person = factory.create(FullName("Harry", "Potter"))
        person.updatedDate shouldBe person.createdDate
    }

    "Different persons can have same name." {
        val name = FullName("Harry", "Potter")
        val person1 = factory.create(name)
        val person2 = factory.create(name)
        person1.name shouldBe person2.name
        person1.id shouldNotBe person2.id
    }

    "Person can change his/her name" {
        val name = FullName("Harry", "Potter")
        val person = swFactory.create(name)
        val id = person.id
        val createdDate = person.createdDate
        person.name shouldBe name

        // 更新日時が新しくなるように時間を進める
        timeFactory.value = timeFactory.value.plusMillis(1)
        val newName = FullName("Ronald", "Weasley")
        person.updateName(newName)
        person.also {
            it.id shouldBe id
            it.name shouldBe newName
            it.createdDate shouldBe createdDate
            it.updatedDate shouldBe timeFactory.value
            it.createdDate shouldNotBe it.updatedDate
        }
    }

    "Person cannot have updated date that is before created date" {
        val person = swFactory.create(FullName("Harry", "Potter"))
        // 更新に失敗するか確認するため時間を巻き戻す
        timeFactory.value = timeFactory.value.minusMillis(1)
        shouldThrow<IllegalPersonStateException> {
            person.updateName(FullName("Ronald", "Weasley"))
        }
    }

})