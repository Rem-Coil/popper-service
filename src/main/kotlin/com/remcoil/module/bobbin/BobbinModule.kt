package com.remcoil.module.bobbin

import com.remcoil.service.action.ActionService
import com.remcoil.service.bobbin.BobbinService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
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

            get("/{taskId}") {
                val bobbins = service.getByTask(call.parameters["taskId"]!!.toInt())
                call.respond(bobbins)
            }
        }
    }
}