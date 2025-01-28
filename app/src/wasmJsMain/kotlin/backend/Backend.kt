package backend

import Json
import exception.ServerException
import io.github.aakira.napier.Napier
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.serialization.SerializationException

abstract class Backend {
    /**
     * Handles any error in the [response]. Will throw an exception if the request is not successful, see throws for
     * more information.
     * @param response The response to process.
     */
    suspend fun errorHandler(response: HttpResponse) {
        if (!response.status.isSuccess()) {
            try {
                val body = response.bodyAsText()
                val error = Json.decodeFromString(Error.serializer(), body)
                throw ServerException.valueOf(error)
            } catch (e: SerializationException) {
                Napier.e(e) { "Could not parse response." }
                throw e
            } catch (e: IllegalArgumentException) {
                Napier.e(e) { "The response given was not JSON." }
                throw e
            }
        }
    }
}
