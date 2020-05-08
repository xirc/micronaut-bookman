package micronaut.bookman.controller.person

data class PatchPersonRequest(
        val firstName: String?,
        val lastName: String?
)