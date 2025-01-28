package navigation

import kotlinx.serialization.Serializable

object Destinations {

    val entries: List<Destination> = listOf()

    @Serializable
    data object NotFound: Destination {
        override val path: String = "/unknown"
    }

}
