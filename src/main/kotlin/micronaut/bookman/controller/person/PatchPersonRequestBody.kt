package micronaut.bookman.controller.person

data class PatchPersonRequestBody(
        val firstName: String?,
        val lastName: String?
)