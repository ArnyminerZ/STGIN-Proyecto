package com.arnyminerz.upv.performance

import org.slf4j.Logger
import org.slf4j.LoggerFactory

val logger: Logger = LoggerFactory.getLogger("PerformanceLogger")

inline fun <Result> measurePerformance(operation: String, block: () -> Result): Result {
    val start = System.currentTimeMillis()
    return block().also {
        logger.debug("$operation took ${System.currentTimeMillis() - start}ms")
    }
}
