package com.remcoil.utils

import com.remcoil.utils.exceptions.DatabaseException
import com.remcoil.utils.exceptions.EntryDoesNotExistException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.serialization.SerializationException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.DateTimeException

suspend inline fun <reified T : Any> ApplicationCall.safetyReceive(onCorrectResult: (T) -> Unit) {
    try {
        kotlin.runCatching { receiveNullable<T>() }.getOrNull()
            ?.let(onCorrectResult)
            ?: respond(HttpStatusCode.BadRequest)

    } catch (e: DatabaseException) {
        respond(HttpStatusCode.BadRequest, e.message.toString())
    } catch (e: SerializationException) {
        respond(HttpStatusCode.BadRequest, e.message.toString())
    } catch (e: DateTimeException) {
        respond(HttpStatusCode.BadRequest, e.message.toString())
    } catch (e: EntryDoesNotExistException) {
        respond(HttpStatusCode.NotFound, e.message.toString())
    }
}

suspend inline fun <reified T> ApplicationCall.respondNullable(message: T?, onNull: HttpStatusCode) {
    if (message == null) {
        this.respond(onNull)
    } else {
        this.respond(message)
    }
}

val Any.logger: Logger get() = LoggerFactory.getLogger(this::class.java)