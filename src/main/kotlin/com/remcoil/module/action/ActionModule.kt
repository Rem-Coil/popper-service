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
    val actionService: ActionService by closestDI().instance()

    routing {
        route("/action") {

            get() {
                val actions = actionService.getAll()
                call.respond(actions)
            }

            get("/bobbin/{bobbin_id}") {
                val actions = actionService.getByBobbinId(call.parameters["bobbin_id"]!!.toInt())
                call.respond(actions)
            }

            delete("/{id}") {
                actionService.deleteAction(call.parameters["id"]!!.toInt())
                call.respond(HttpStatusCode.OK)
            }

            post {
                call.safetyReceive<Action> { action ->
                    call.respond(actionService.createAction(action))
                }
            }

            put {
                call.safetyReceive<Action> { action ->
                    actionService.updateAction(action)
                    call.respond(HttpStatusCode.OK)
                }
            }

            route("/full") {
                get() {
                    val actions = actionService.getAllFull()
                    call.respond(actions)
                }

                get("/task/{task_id}") {
                    val actions = actionService.getFullByTaskId(call.parameters["task_id"]!!.toInt())
                    call.respond(actions)
                }

                get("/batch/{batch_id}") {
                    val actions = actionService.getFullByBatchId(call.parameters["batch_id"]!!.toLong())
                    call.respond(actions)
                }

                get("/bobbin/{bobbin_id}") {
                    val actions = actionService.getFullByBobbinId(call.parameters["bobbin_id"]!!.toInt())
                    call.respond(actions)
                }

                get("/{id}") {
                    val action = actionService.getFullById(call.parameters["id"]!!.toInt())
                    call.respond(action ?: HttpStatusCode.BadRequest)
                }
            }
        }
    }
}