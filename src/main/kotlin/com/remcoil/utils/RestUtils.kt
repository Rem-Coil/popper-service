package com.remcoil.utils

import com.remcoil.utils.exceptions.DatabaseException
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import java.sql.SQLException

suspend inline fun <reified T : Any> ApplicationCall.safetyReceive(onCorrectResult: (T) -> Unit) {
    try {
        receiveOrNull<T>()
            ?.let(onCorrectResult)
            ?: respond(HttpStatusCode.BadRequest)

    } catch (e: DatabaseException) {
        respond(HttpStatusCode.BadRequest, e.message.toString())
    } catch (e: RuntimeException) {
        respond(HttpStatusCode.BadRequest, e.message.toString())
    }
}