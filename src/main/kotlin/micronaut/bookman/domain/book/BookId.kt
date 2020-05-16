package micronaut.bookman.domain.book

import micronaut.bookman.exceptions.AppIllegalArgumentException
import java.lang.IllegalArgumentException
import java.util.*

data class BookId private constructor(
        private val value: UUID
) {
    constructor(): this(UUID.randomUUID())

    override fun toString(): String {
        return value.toString()
    }

    companion object {
        fun fromString(value: String): BookId {
            try {
                val uuid = UUID.fromString(value)
                return BookId(uuid)
            } catch (e: IllegalArgumentException) {
                throw AppIllegalArgumentException("Illegal Book ID string")
            }
        }
    }
}