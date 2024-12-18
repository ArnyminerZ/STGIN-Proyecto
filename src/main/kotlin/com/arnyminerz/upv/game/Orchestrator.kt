package com.arnyminerz.upv.game

import kotlinx.coroutines.flow.MutableSharedFlow

object Orchestrator {
    val actionsFlow = MutableSharedFlow<GameAction>()
}
