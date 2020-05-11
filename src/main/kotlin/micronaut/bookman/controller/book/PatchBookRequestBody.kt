package micronaut.bookman.controller.book

class PatchBookRequestBody(
        val title: String?,
        val authorIds: List<String>?
)