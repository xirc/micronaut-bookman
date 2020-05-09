package micronaut.bookman.domain.time

import org.joda.time.DateTime

interface DateTimeFactory {
    fun now(): DateTime
}