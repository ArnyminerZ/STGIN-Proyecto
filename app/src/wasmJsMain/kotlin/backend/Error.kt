package backend

import kotlinx.serialization.Serializable

@Serializable
sealed class Error(
    val code: Int,
    val message: String,
)
