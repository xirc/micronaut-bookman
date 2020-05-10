package micronaut.bookman.controller.book

import micronaut.bookman.controller.ErrorResponseBody
import micronaut.bookman.usecase.BookCollectionDto

data class BookCollectionResponse private constructor(val value: BookCollectionDto?, val error: ErrorResponseBody?) {
    companion object {
        fun success(value: BookCollectionDto) = BookCollectionResponse(value, null)
        fun failure(error: ErrorResponseBody) = BookCollectionResponse(null, error)
    }
}