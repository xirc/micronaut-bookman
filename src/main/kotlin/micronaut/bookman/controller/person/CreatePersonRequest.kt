package micronaut.bookman.controller.person

data class CreatePersonRequest(
        val firstName: String? = null,
        val lastName: String? = null
)