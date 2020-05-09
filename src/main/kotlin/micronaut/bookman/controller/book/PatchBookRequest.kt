package micronaut.bookman.controller.book

class PatchBookRequest(
        val title: String?,
        val authorId: String?
)