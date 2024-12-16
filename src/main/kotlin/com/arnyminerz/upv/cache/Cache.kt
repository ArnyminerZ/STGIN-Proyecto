package com.arnyminerz.upv.cache

object Cache {
    private const val CACHE_TYPE_MEMORY = "memory"
    private const val CACHE_TYPE_REDIS = "redis"

    private var cacheType: String = CACHE_TYPE_MEMORY

    @Volatile
    private var memoryCache = mapOf<String, String>()

    fun initialize() {
        cacheType = System.getenv("CACHE_TYPE") ?: CACHE_TYPE_MEMORY
        val host = System.getenv("CACHE_HOST").takeUnless { it.isNullOrBlank() }
        val port = System.getenv("CACHE_PORT")?.toIntOrNull() ?: 6379

        when (cacheType) {
            CACHE_TYPE_MEMORY -> {
                memoryCache = emptyMap()
            }

            CACHE_TYPE_REDIS -> {
                require(host != null) { "CACHE_HOST is required for Redis" }

                TODO("Redis not yet implemented")
            }
        }
    }

    suspend fun set(key: String, value: String) {
        when (cacheType) {
            CACHE_TYPE_MEMORY -> memoryCache += key to value
            CACHE_TYPE_REDIS -> TODO("Redis not yet implemented")

            else -> error("Unknown cache type $cacheType")
        }
    }

    suspend fun get(key: String): String? {
        return when (cacheType) {
            CACHE_TYPE_MEMORY -> memoryCache[key]
            CACHE_TYPE_REDIS -> TODO("Redis not yet implemented")

            else -> error("Unknown cache type $cacheType")
        }
    }
}
