package com.remcoil.module.action

import com.remcoil.data.model.action.Action
import com.remcoil.service.action.ActionService
import com.remcoil.utils.safetyReceive
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

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
                call.safetyReceive<Action> { action ->
                    call.respond(service.createAction(action)?: HttpStatusCode.Forbidden)
                }
            }

            put {
                call.safetyReceive<Action> { action ->
                    service.updateAction(action)
                    call.respond(HttpStatusCode.OK)
                }
            }
        }
    }
}