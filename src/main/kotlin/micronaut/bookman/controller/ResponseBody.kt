package micronaut.bookman.controller

data class ResponseBody<out T>(
        val value: T?,
        val error: ErrorResponseBody?
) {
    companion object {
        fun <T> success(value: T) = ResponseBody(value, null)
        fun <T> failure(e: ErrorResponseBody) = ResponseBody<T>(null, e)
    }
}