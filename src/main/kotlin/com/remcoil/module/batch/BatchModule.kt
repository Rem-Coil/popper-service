package com.remcoil.module.batch

import com.remcoil.data.model.batch.Batch
import com.remcoil.service.batch.BatchService
import com.remcoil.utils.safetyReceive
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
                val batches = batchService.getAll()
                call.respond(batches)
            }

            get("/{id}") {
                val batch = batchService.getById(call.parameters["id"]!!.toLong())
                call.respond(batch ?: HttpStatusCode.BadRequest)
            }

            post {
                call.safetyReceive<Batch> { batch ->
                    call.respond(batchService.createBatch(batch))
                }
            }

            delete("/{id}") {
                batchService.deleteBatchById(call.parameters["id"]!!.toLong())
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}