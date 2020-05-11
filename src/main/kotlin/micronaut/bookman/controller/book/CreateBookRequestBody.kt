package micronaut.bookman.controller.book

data class CreateBookRequestBody(
        val title: String? = null,
        val authorIds: List<String>? = null
)