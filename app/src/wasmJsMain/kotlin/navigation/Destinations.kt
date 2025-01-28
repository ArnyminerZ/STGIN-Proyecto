package navigation

import kotlinx.serialization.Serializable

object Destinations {

    val entries: List<Destination> = listOf(Main, Auth)

    @Serializable
    data object Main : Destination {
        override val path: String = "/"
    }

    @Serializable
    data object Auth : Destination {
        override val path: String = "/auth"
    }

    @Serializable
    data object NotFound : Destination {
        override val path: String = "/unknown"
    }

}
