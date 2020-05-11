package micronaut.bookman.infra.exceptions

// InfraException はアプリケーションの例外として処理したくないので Throwable を継承させる
open class InfraException(
        override val message: String?,
        override val cause: Throwable?
) : Throwable(message, cause) {
    constructor(message: String?) : this(message, null)
    constructor(cause: Throwable?) : this(cause?.toString(), cause)
    constructor() : this(null, null)
}