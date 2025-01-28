package model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import backend.AuthBackend
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

abstract class BaseModel : ViewModel() {
    private var onNotLoggedIn: () -> Unit = {}

    protected open suspend fun performLoad() {

    }

    protected fun launch(block: suspend CoroutineScope.() -> Unit): Job {
        return viewModelScope.launch(block = block)
    }

    fun load(
        onNotLoggedIn: () -> Unit = {},
        onAlreadyLoggedIn: () -> Unit = {},
    ) {
        this.onNotLoggedIn = onNotLoggedIn

        launch {
            if (!AuthBackend.isLoggedIn()) {
                Napier.w { "User not logged in" }
                onNotLoggedIn()
                return@launch
            }

            onAlreadyLoggedIn()
            performLoad()
        }
    }

    protected fun <Type> Flow<Type>.stateIn(initialValue: Type) = stateIn(viewModelScope, WhileSubscribed(5_000), initialValue)
}
