package exception

import backend.Error
import error.ErrorCodes
import exception.server.MissingCredentialsException
import exception.server.UserAlreadyExistsException
import exception.server.WrongPasswordException

@Suppress("CanBeParameter", "MemberVisibilityCanBePrivate")
open class ServerException(
    val errorCode: Int? = null,
    val errorMessage: String? = null,
) : RuntimeException("#$errorCode: $errorMessage") {
    companion object {
        private val errorMatching: Map<Int, () -> Exception> = mapOf(
            ErrorCodes.MISSING_CREDENTIALS to { MissingCredentialsException() },
            ErrorCodes.INVALID_CREDENTIALS to { WrongPasswordException() },
            ErrorCodes.USER_ALREADY_EXISTS to { UserAlreadyExistsException() }
        )

        fun valueOf(error: Error): Exception {
            return errorMatching[error.code]?.invoke() ?: ServerException(error.code, error.message)
        }
    }
}
