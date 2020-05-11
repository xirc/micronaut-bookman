package micronaut.bookman.exceptions

enum class ErrorCode(value: Int) {
    // Book 1000 ~ 1999
    BOOK_NOT_FOUND(1000),
    DUPLICATE_BOOK(1001),
    ILLEGAL_BOOK_STATE(1002),
    // Person 2000 ~ 2999
    PERSON_NOT_FOUND(2000),
    DUPLICATE_PERSON(2001),
    ILLEGAL_PERSON_STATE(2002),
}