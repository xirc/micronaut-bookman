package micronaut.bookman.domain.time

import io.micronaut.context.annotation.Primary
import org.joda.time.DateTime
import javax.inject.Singleton

@Primary
@Singleton
class ServerDateTimeFactory : DateTimeFactory {
    override fun now(): DateTime = DateTime.now()
}