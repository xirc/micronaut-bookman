package micronaut.bookman.controller.book

import micronaut.bookman.controller.ErrorResponseBody
import micronaut.bookman.usecase.LibrarianBookUseCase

data class BookResponse private constructor(val value: BookResponseBody?, val error: ErrorResponseBody?) {
    companion object {
        fun success(book: LibrarianBookUseCase.BookDTO) = BookResponse(BookResponseBody.createFrom(book.book, book.author), null)
        fun failure(error: ErrorResponseBody) = BookResponse(null, error)
    }
}