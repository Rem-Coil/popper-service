package com.remcoil.module.action

import com.remcoil.data.model.action.Action
import com.remcoil.data.model.action.DefectsComment
import com.remcoil.service.action.ActionService
import com.remcoil.utils.safetyReceive
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
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
                val actions = actionService.getByBobbinId(call.parameters["bobbin_id"]!!.toLong())
                call.respond(actions)
            }

            authenticate("jwt-access") {
                delete("/{id}") {
                    actionService.deleteAction(call.parameters["id"]!!.toLong())
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
            }
            route("/comment") {
                get {
                    val comments = actionService.getAllComments()
                    call.respond(comments)
                }
                get("/{action_id}") {
                    val comment = actionService.getCommentByActionId(call.parameters["action_id"]!!.toLong())
                    call.respond(comment ?: HttpStatusCode.BadRequest)
                }
                authenticate("jwt-access") {
                    post {
                        call.safetyReceive<DefectsComment> { comment ->
                            call.respond(actionService.createComment(comment))
                        }
                    }
                    put {
                        call.safetyReceive<DefectsComment> { comment ->
                            actionService.updateComment(comment)
                            call.respond(HttpStatusCode.OK)
                        }
                    }
                    delete("/{action_id}") {
                        actionService.deleteComment(call.parameters["action_id"]!!.toLong())
                        call.respond(HttpStatusCode.OK)
                    }
                }
            }

            route("/full") {
                get {
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
                    val actions = actionService.getFullByBobbinId(call.parameters["bobbin_id"]!!.toLong())
                    call.respond(actions)
                }

                get("/{id}") {
                    val action = actionService.getFullById(call.parameters["id"]!!.toLong())
                    call.respond(action ?: HttpStatusCode.BadRequest)
                }
            }
        }
    }
}