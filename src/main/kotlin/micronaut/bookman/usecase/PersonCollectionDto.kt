package micronaut.bookman.usecase

data class PersonCollectionDto(
        val persons: List<PersonDto>,
        val pageCount: Long
)