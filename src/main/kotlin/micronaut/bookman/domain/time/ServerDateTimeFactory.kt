package micronaut.bookman.domain.time

import org.joda.time.DateTime

class ServerDateTimeFactory : DateTimeFactory {
    override fun now(): DateTime = DateTime.now()
}