package com.remcoil.module

import com.remcoil.service.BatchService
import com.remcoil.utils.exceptions.EntryDoesNotExistException
import com.remcoil.utils.respondNullable
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

fun Application.batchModule() {
    val batchService: BatchService by closestDI().instance()

    routing {
        route("/batch") {
            get {
                val batches = batchService.getAllBatches()
                call.respond(batches)
            }

            get("/{id}") {
                try {
                    val batch = call.parameters["id"]?.toLongOrNull()?.let {
                        batchService.getBatchById(it)
                    }
                    call.respondNullable(batch, onNull = HttpStatusCode.BadRequest)
                } catch (e: EntryDoesNotExistException) {
                    call.respond(HttpStatusCode.NotFound, e.message.toString())
                }
            }
        }
    }
}