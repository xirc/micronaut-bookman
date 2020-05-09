package micronaut.bookman.controller.book

import micronaut.bookman.controller.ErrorResponseBody
import micronaut.bookman.usecase.BookDto

data class BookResponse private constructor(val value: BookDto?, val error: ErrorResponseBody?) {
    companion object {
        fun success(value: BookDto) = BookResponse(value, null)
        fun failure(error: ErrorResponseBody) = BookResponse(null, error)
    }
}