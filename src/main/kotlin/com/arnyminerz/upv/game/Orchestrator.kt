package com.arnyminerz.upv.game

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object Orchestrator {
    private val _actionsFlow = MutableSharedFlow<GameAction>()
    val actionsFlow get() = _actionsFlow.asSharedFlow()

    private val _stateFlow = MutableSharedFlow<GameState>()
    val stateFlow get() = _stateFlow.asSharedFlow()

    suspend fun notifyAction(matchId: Int, type: GameAction.Type) {
        _actionsFlow.emit(GameAction(matchId, type))
    }

    suspend fun notifyState(matchId: Int, state: GameState.State) {
        _stateFlow.emit(GameState(matchId, state))
    }
}
