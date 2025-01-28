package model

import backend.AuthBackend
import exception.ServerException
import exception.server.UserAlreadyExistsException
import exception.server.WrongPasswordException
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow

class AuthModel : BaseModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading get() = _isLoading.stateIn(false)

    fun login(username: String, password: String, onSuccess: () -> Unit) {
        launch {
            try {
                Napier.e { "Logging in as $username..." }
                AuthBackend.login(username, password)
                onSuccess()
            } catch (e: WrongPasswordException) {
                Napier.e(e) { "Could not log in" }
            } catch (e: ServerException) {
                Napier.e(e) { "Could not log in" }
            }
        }
    }

    fun register(username: String, password: String, onSuccess: () -> Unit) {
        launch {
            try {
                _isLoading.emit(true)
                Napier.e { "Registering as $username..." }
                AuthBackend.register(username, password)
                onSuccess()
            } catch (e: UserAlreadyExistsException) {
                Napier.e(e) { "Could not register" }
            } catch (e: ServerException) {
                Napier.e(e) { "Could not register" }
            } finally {
                _isLoading.emit(false)
            }
        }
    }
}
