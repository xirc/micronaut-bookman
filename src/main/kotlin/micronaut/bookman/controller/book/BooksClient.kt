package micronaut.bookman.controller.book

import io.micronaut.http.client.annotation.Client

@Client("/books")
interface BooksClient : BooksApi