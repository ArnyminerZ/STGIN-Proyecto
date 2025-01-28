package exception

open class ServerException(
    message: String,
    val errorCode: Int? = null,
    val errorMessage: String? = null,
) : RuntimeException(message) {
    constructor(errorResponse: ErrorResponse) : this(
        "Error ${errorResponse.code}: ${errorResponse.message}",
        errorResponse.code,
        errorResponse.message,
    )
}
