package micronaut.bookman.exceptions

open class ApplicationException(
        open val code: ErrorCode,
        override val message: String?,
        override val cause: Throwable?
) : Throwable(message, cause) {
    constructor(code: ErrorCode, message: String?) : this(code, message, null)
    constructor(code: ErrorCode, cause: Throwable?) : this(code, cause?.toString(), cause)
    constructor(code: ErrorCode) : this(code, null, null)
}