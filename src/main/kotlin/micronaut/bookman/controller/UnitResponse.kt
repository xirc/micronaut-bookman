package micronaut.bookman.controller

class UnitResponse private constructor(val error: ErrorResponseBody?) {
    companion object {
        fun success() = UnitResponse(null)
        fun failure(e: ErrorResponseBody) = UnitResponse(e)
    }
}