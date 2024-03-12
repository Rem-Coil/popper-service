package com.remcoil.utils

import com.remcoil.utils.exceptions.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*

suspend inline fun <reified T : Any> ApplicationCall.safetyReceive(onCorrectResult: (T) -> Unit) {
    try {
        receive<T>().let(onCorrectResult)
    } catch (e: Exception) {
        when (e) {
            is EntryDoesNotExistException -> respond(HttpStatusCode.NotFound, e.message.toString())
            is DuplicateValueException -> respond(HttpStatusCode.Conflict, e.message.toString())

            is UnsupportedOperationTypeException,
            is LockedProductException,
            is InActiveProductException,
            is UnLockedProductException,
            is DatabaseException,
            is WrongParamException,
            is BadRequestException -> respond(HttpStatusCode.BadRequest, e.message.toString())
        }
    }
}

suspend inline fun <reified T> ApplicationCall.respondNullable(message: T?, onNull: HttpStatusCode) {
    if (message == null) {
        this.respond(onNull)
    } else {
        this.respond(message)
    }
}