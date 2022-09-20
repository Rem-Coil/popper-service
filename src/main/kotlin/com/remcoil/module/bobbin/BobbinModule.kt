package com.remcoil.module.bobbin

import com.remcoil.data.model.bobbin.Bobbin
import com.remcoil.service.bobbin.BobbinService
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
        route("/bobbin") {
            get {
                val bobbins = bobbinService.getAll()
                call.respond(bobbins)
            }

            get("/{id}") {
                val bobbin = bobbinService.getById(call.parameters["id"]!!.toLong())
                call.respond(bobbin ?: HttpStatusCode.BadRequest)
            }

            get("/batch/{batch_id}") {
                val bobbins = bobbinService.getByBatchId(call.parameters["batch_id"]!!.toLong())
                call.respond(bobbins)
            }

            post {
                call.safetyReceive<Bobbin> { bobbin ->
                    val createdBobbin = bobbinService.createBobbin(bobbin)
                    call.respond(createdBobbin)
                }
            }

            delete("/{id}") {
                bobbinService.deactivateById(call.parameters["id"]!!.toLong())
                call.respond(HttpStatusCode.OK)
            }

            delete("delete/{id}") {
                bobbinService.trueDeleteById(call.parameters["id"]!!.toLong())
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}

