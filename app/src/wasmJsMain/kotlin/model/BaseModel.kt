package model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow

abstract class BaseModel : ViewModel() {
    private var onNotLoggedIn: () -> Unit = {}

    protected open suspend fun performLoad() {
        val userData = UserBackend.getUserData()
        // If user is not an admin, logout
        if (!userData.isAdmin()) {
            AuthBackend.logout()
            onNotLoggedIn()
            return
        }

        userData.store()
    }

    fun load(onNotLoggedIn: () -> Unit) {
        this.onNotLoggedIn = onNotLoggedIn

        viewModelScope.launch {
            if (!AuthBackend.isLoggedIn()) {
                Napier.w { "User not logged in" }
                onNotLoggedIn()
                return@launch
            }
        }
    }

    protected fun <Type> Flow<Type>.stateIn(initialValue: Type) = stateIn(viewModelScope, WhileSubscribed(5_000), initialValue)
}
