package com.arnyminerz.upv.error

import io.ktor.http.HttpStatusCode
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
sealed class Error(
    val code: Int,
    val message: String,
    @Transient
    val httpStatusCode: HttpStatusCode = HttpStatusCode.BadRequest
)
