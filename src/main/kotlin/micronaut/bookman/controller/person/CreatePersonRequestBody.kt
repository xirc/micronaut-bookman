package micronaut.bookman.controller.person

data class CreatePersonRequestBody(
        val firstName: String? = null,
        val lastName: String? = null
)