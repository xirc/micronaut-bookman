package micronaut.bookman.domain.book

import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.StringSpec
import io.micronaut.test.annotation.MicronautTest
import micronaut.bookman.domain.ConstantDateTimeFactory
import micronaut.bookman.domain.book.error.IllegalBookStateException
import micronaut.bookman.domain.time.ServerDateTimeFactory
import org.joda.time.DateTime
import java.util.*

@MicronautTest
class BookTest : StringSpec({
    // NOTE: デフォルト
    val factory = Book.Factory(ServerDateTimeFactory())

    // NOTE: 時刻を厳密に指定したいところで使う
    // 誤って DateTime.now() が使われたときにわかるように1日程度巻き戻しておく
    val timeFactory = ConstantDateTimeFactory(DateTime.now().minusDays(1))
    val swFactory = Book.Factory(timeFactory)

    "Book has empty default title" {
        val book = factory.create()
        book.title shouldBe ""
    }

    "Book has created date that is exactly same provided by timeFactory" {
        val book = swFactory.create()
        book.createdDate shouldBe timeFactory.value
    }

    "Book has updated date that is exactly same provided by timeFactory" {
        val book = swFactory.create()
        book.updatedDate shouldBe timeFactory.value
    }

    "Book has updated date that should be exactly equal to created date as default" {
        val book = factory.create()
        book.updatedDate shouldBe book.createdDate
    }

    "Book can change its title" {
        val book = swFactory.create()
        // 更新日時が新しくなるか確認するため時間を進める
        timeFactory.value = timeFactory.value.plusMillis(1)
        val title = "TITLE (${UUID.randomUUID()})"
        book.updateTitle(title)
        book.title shouldBe title
        book.updatedDate shouldBe timeFactory.value
    }

    "Book cannot have updated date that is before created date" {
        val book = swFactory.create()
        // 更新に失敗するか確認するため時間を巻き戻す
        timeFactory.value = timeFactory.value.minusMillis(1)
        shouldThrow<IllegalBookStateException> {
            book.updateTitle("TITLE ${UUID.randomUUID()}")
        }
    }

})