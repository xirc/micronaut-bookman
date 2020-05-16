package micronaut.bookman.domain.person

import micronaut.bookman.exceptions.AppIllegalArgumentException
import java.lang.IllegalArgumentException
import java.util.*

data class PersonId private constructor(
        private val value: UUID
) {
    constructor(): this(UUID.randomUUID())

    override fun toString(): String {
        return value.toString()
    }
    companion object {
        fun fromString(value: String): PersonId {
            try {
                val uuid = UUID.fromString(value)
                return PersonId(uuid)
            } catch (e: IllegalArgumentException) {
                throw AppIllegalArgumentException("Illegal Person ID string")
            }
        }
    }
}