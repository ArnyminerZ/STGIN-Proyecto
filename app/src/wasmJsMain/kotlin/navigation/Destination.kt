package navigation

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
sealed interface Destination {
    @Transient
    val path: String
}
