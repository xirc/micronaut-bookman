package micronaut.bookman.controller.book

data class CreateBookRequest(
        val title: String? = null,
        val authorIds: List<String>? = null
)