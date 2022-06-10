package com.remcoil.utils

import com.remcoil.utils.exceptions.DatabaseException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

suspend inline fun <reified T : Any> ApplicationCall.safetyReceive(onCorrectResult: (T) -> Unit) {
    try {
        receiveOrNull<T>()
            ?.let(onCorrectResult)
            ?: respond(HttpStatusCode.BadRequest)

    } catch (e: DatabaseException) {
        respond(HttpStatusCode.BadRequest, e.message.toString())
    }
}

val Any.logger: Logger get() = LoggerFactory.getLogger(this::class.java)