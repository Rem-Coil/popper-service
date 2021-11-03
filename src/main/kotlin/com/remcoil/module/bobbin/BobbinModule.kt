package com.remcoil.module.bobbin

import com.remcoil.service.bobbin.BobbinService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import org.kodein.di.*
import org.kodein.di.ktor.closestDI

fun Application.bobbinModule() {
    val service: BobbinService by closestDI().instance()

    routing {
        route("/bobbin") {

            get {
                val bobbins = service.getAll()
                call.respond(bobbins)
            }

            get("/{id}") {
                val bobbin = service.getById(call.parameters["id"]!!.toInt())
                call.respond(bobbin ?: HttpStatusCode.BadRequest)
            }

            get("/task/{id}") {
                val bobbins = service.getByTask(call.parameters["id"]!!.toInt())
                call.respond(bobbins)
            }
        }
    }
}