package com.arnyminerz.upv.endpoint.game

import com.arnyminerz.upv.ai.MachineActions
import com.arnyminerz.upv.database.ServerDatabase
import com.arnyminerz.upv.database.entity.Match
import com.arnyminerz.upv.endpoint.type.Websocket
import com.arnyminerz.upv.endpoint.type.WebsocketContext
import com.arnyminerz.upv.exception.ForbiddenPositionException
import com.arnyminerz.upv.exception.NotYourTurnException
import com.arnyminerz.upv.exception.PositionOutOfBoundsException
import com.arnyminerz.upv.game.*
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.uuid.ExperimentalUuidApi
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.filter
import org.slf4j.LoggerFactory

@OptIn(ExperimentalEncodingApi::class, ExperimentalUuidApi::class)
object GameEndpoint : Websocket("/api/matches/{id}/socket") {

    private val logger = LoggerFactory.getLogger(GameEndpoint::class.java)

    override suspend fun WebsocketContext.body(userId: String) {
        val matchId = call.parameters["id"]?.toIntOrNull()
        if (matchId == null) {
            close("INVALID_MATCH_ID")
        }
        matchId!! // won't be null

        val match = ServerDatabase { Match.findById(matchId) }
        if (match == null) {
            close("MATCH_NOT_FOUND")
        }
        match!! // won't be null

        val player = match.player(userId)
        if (player == null) {
            close("NOT_YOUR_MATCH")
        }
        player!! // Not null

        val actionsObserver = launch {
            Orchestrator.actionsFlow.filter { it.matchId == matchId }.collect { action ->
                send(action.toString())
            }
        }
        val stateObserver = launch {
            Orchestrator.stateFlow.filter { it.matchId == matchId }.collect { action ->
                send(action.toString())
            }
        }

        runCatching {
            session.incoming.consumeEach { frame ->
                if (frame is Frame.Text) {
                    val receivedText = frame.readText()
                    try {
                        val action = GameAction.fromString(receivedText)
                        when (action.type) {
                            is GameAction.Type.DropBomb -> bomb(action.matchId, action.type.copy(player = player))
                        }
                    } catch (e: IllegalArgumentException) {
                        logger.error("Could not parse action.", e)
                    } catch (e: IllegalStateException) {
                        logger.error("Could not parse action.", e)
                    } catch (e: NotYourTurnException) {
                        logger.error("Not your turn", e)
                    } catch (e: PositionOutOfBoundsException) {
                        logger.error("Position out of bounds.", e)
                    } catch (e: ForbiddenPositionException) {
                        logger.error("Position is forbidden.", e)
                    } catch (e: ClassNotFoundException) {
                        logger.error("Match not found.", e)
                    }
                }
            }
        }
            .onFailure { exception -> logger.error("Got an exception on the game endpoint.", exception) }
            .also {
                actionsObserver.cancel()
                stateObserver.cancel()
            }
    }

    private suspend fun bomb(matchId: Int, type: GameAction.Type.DropBomb): Boolean {
        val match = ServerDatabase { Match.findById(matchId) }
        if (match == null) {
            throw ClassNotFoundException("Could not find match with id $matchId")
        }

        val player = type.player
        val position = type.position

        val isVsMachine = ServerDatabase { match.user2 == null }

        var game = bomb(matchId, match.game, player, position, isVsMachine)

        // If the second player is a machine, perform a bombing
        if (isVsMachine) {
            game = MachineActions.aiBomb(match.id.value, game)
        }

        val hit = game.setup(player.other()).hitsAnyBoat(position)

        // Update the game in the database
        ServerDatabase { match.game = game }

        // Notify the action made
        Orchestrator.notifyAction(matchId, type)

        val winner = game.isOver()
        val state = if (winner == null) {
            GameState.State.Playing
        } else {
            GameState.State.Ended(winner)
        }
        Orchestrator.notifyState(matchId, state)

        return hit
    }

    private suspend fun bomb(
        matchId: Int,
        game: Game,
        player: Player,
        position: Position,
        isVsMachine: Boolean
    ): Game {
        return try {
            game.bomb(matchId, player, position)
        } catch (_: NotYourTurnException) {
            // If not the user's turn, but vs a machine, it means that for some reason the movement of the machine
            // was not made. Make it and call the body again
            if (isVsMachine) {
                // perform a bombing as the AI
                val newGame = MachineActions.aiBomb(matchId, game)
                // call bomb again
                bomb(matchId, newGame, player, position, true)
            } else {
                throw NotYourTurnException()
            }
        } catch (error: PositionOutOfBoundsException) {
            throw error
        } catch (error: ForbiddenPositionException) {
            throw error
        }
    }
}
