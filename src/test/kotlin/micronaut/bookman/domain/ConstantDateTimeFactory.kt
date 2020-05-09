package micronaut.bookman.domain

import micronaut.bookman.domain.time.DateTimeFactory
import org.joda.time.DateTime

class ConstantDateTimeFactory(var value: DateTime) : DateTimeFactory {
    override fun now(): DateTime = value
}