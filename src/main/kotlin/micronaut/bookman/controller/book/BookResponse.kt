package micronaut.bookman.controller.book

import micronaut.bookman.controller.ErrorResponseBody
import micronaut.bookman.domain.book.Book

data class BookResponse private constructor(val value: BookResponseBody?, val error: ErrorResponseBody?) {
    companion object {
        fun success(book: Book) = BookResponse(BookResponseBody.createFrom(book), null)
        fun failure(error: ErrorResponseBody) = BookResponse(null, error)
    }
}