package com.remcoil.module.bobbin

import com.remcoil.data.model.bobbin.Bobbin
import com.remcoil.service.bobbin.BobbinService
import com.remcoil.utils.exceptions.EntryDoesNotExistException
import com.remcoil.utils.safetyReceive
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.kodein.di.instance

import org.kodein.di.ktor.closestDI

fun Application.bobbinModule() {
    val bobbinService: BobbinService by closestDI().instance()

    routing {

        get("/batch/bobbins/{batch_id}") {
            val bobbins = call.parameters["batch_id"]?.let { id ->
                id.toLongOrNull()?.let {
                    bobbinService.getByBatchId(id.toLong())
                }
            }
            call.respond(bobbins ?: HttpStatusCode.BadRequest)
        }

        route("/bobbin") {
            get {
                val bobbins = bobbinService.getAll()
                call.respond(bobbins)
            }

            get("/{id}") {
                try {
                    val bobbin = call.parameters["id"]?.let { id ->
                        id.toLongOrNull()?.let {
                            bobbinService.getById(id.toLong())
                        }
                    }
                    call.respond(bobbin ?: HttpStatusCode.BadRequest)
                } catch (e: EntryDoesNotExistException) {
                    call.respond(HttpStatusCode.NotFound, e.message.toString())
                }
            }

            post {
                call.safetyReceive<Bobbin> { bobbin ->
                    val createdBobbin = bobbinService.createBobbin(bobbin)
                    call.respond(createdBobbin)
                }
            }

            delete("/{id}") {
                try {
                    val result = call.parameters["id"]?.let { id ->
                        id.toLongOrNull()?.let {
                            bobbinService.deactivateById(id.toLong())
                        }
                    }
                    call.respond(
                        if (result == null) {
                            HttpStatusCode.BadRequest
                        } else {
                            HttpStatusCode.OK
                        }
                    )
                } catch (e: EntryDoesNotExistException) {
                    call.respond(HttpStatusCode.NotFound, e.message.toString())
                }
            }

            delete("{id}/delete") {
                val result = call.parameters["id"]?.let { id ->
                    id.toLongOrNull()?.let {
                        bobbinService.trueDeleteById(id.toLong())
                    }
                }
                call.respond(
                    if (result == null) {
                        HttpStatusCode.BadRequest
                    } else {
                        HttpStatusCode.OK
                    }
                )
            }
        }
    }
}

