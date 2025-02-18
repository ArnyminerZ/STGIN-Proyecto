package com.arnyminerz.upv.endpoint.game

import Endpoints
import com.arnyminerz.upv.database.ServerDatabase
import com.arnyminerz.upv.database.entity.User
import com.arnyminerz.upv.database.table.Users
import com.arnyminerz.upv.endpoint.type.EndpointContext
import com.arnyminerz.upv.endpoint.type.SecureEndpoint
import io.ktor.http.HttpMethod
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer

object OpponentsListEndpoint : SecureEndpoint(Endpoints.Game.OPPONENTS, HttpMethod.Get) {
    override suspend fun EndpointContext.secureBody(userId: String) {
        val users = ServerDatabase { User.find { Users.id neq userId }.map { it.id.value } }
        respondSuccess(users, ListSerializer(String.serializer()))
    }
}
