package com.remcoil.module.batch

import com.remcoil.data.model.batch.BatchIdentity
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

            get("task/{task_id}") {
                val batches = batchService.getByTaskId(call.parameters["task_id"]!!.toInt())
                call.respond(batches)
            }

            post {
                call.safetyReceive<BatchIdentity> { batchIdentity ->
                    call.respond(batchService.createByIdentity(batchIdentity) ?: HttpStatusCode.BadRequest)
                }
            }

            delete("/{id}") {
                batchService.deleteBatchById(call.parameters["id"]!!.toLong())
                call.respond(HttpStatusCode.OK)
            }

            route("/full") {
                get {
                    val batches = batchService.getAllFull()
                    call.respond(batches)
                }

                get("/{id}") {
                    val batch = batchService.getFullById(call.parameters["id"]!!.toLong())
                    call.respond(batch)
                }
            }
        }
    }
}