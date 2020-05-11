package micronaut.bookman.error

enum class ErrorCode(value: Int) {
    // Book 1000 ~ 1999
    BOOK_NOT_FOUND(1000),
    // Person 2000 ~ 2999
    PERSON_NOT_FOUND(2000)
}