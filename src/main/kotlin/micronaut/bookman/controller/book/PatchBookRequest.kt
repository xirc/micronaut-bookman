package micronaut.bookman.controller.book

class PatchBookRequest(
        val title: String?,
        val authorIds: List<String>?
)