package com.remcoil.module.action

import com.remcoil.service.action.ActionService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

fun Application.actionModule() {
    val service: ActionService by closestDI().instance()

    routing {
        route("/action") {

            get() {
                val actions = service.getAll()
                call.respond(actions)
            }

            get("/task/{taskId}") {
                val actions = service.getByTaskId(call.parameters["taskId"]!!.toInt())
                call.respond(actions)
            }

            get("/bobbin/{bobbinId}") {
                val actions = service.getByBobbinId(call.parameters["bobbinId"]!!.toInt())
                call.respond(actions)
            }

            delete("/{id}") {
                service.deleteAction(call.parameters["id"]!!.toInt())
                call.respond(HttpStatusCode.OK)
            }

            post {
                val action = service.createAction(call.receive())
                call.respond(action?: HttpStatusCode.Forbidden)
            }

            put {
                service.updateAction(call.receive())
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}