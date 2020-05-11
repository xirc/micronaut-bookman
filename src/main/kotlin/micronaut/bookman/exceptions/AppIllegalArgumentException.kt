package micronaut.bookman.exceptions

class AppIllegalArgumentException(
        override val message: String
): ApplicationException(ErrorCode.APP_ILLEGAL_ARGUMENT, message)