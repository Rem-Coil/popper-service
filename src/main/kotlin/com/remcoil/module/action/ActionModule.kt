package com.remcoil.module.action

import com.remcoil.data.model.action.*
import com.remcoil.service.action.ActionService
import com.remcoil.service.action.FullActionService
import com.remcoil.utils.exceptions.InActiveProductException
import com.remcoil.utils.safetyReceive
import com.remcoil.utils.safetyRespond
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

fun Application.actionModule() {
    val actionService: ActionService by closestDI().instance()
    val fullActionService: FullActionService by closestDI().instance()

    routing {
        route("/action") {
            get() {
                val actions = actionService.getAllActions()
                call.respond(actions)
            }


            get("/product/{product_id}") {
                val actions = call.parameters["product_id"]?.let {productId ->
                    productId.toLongOrNull()?.let {
                        actionService.getByProductId(productId.toLong())
                    }
                }
                call.safetyRespond(actions, HttpStatusCode.BadRequest)
            }

            put {
                call.safetyReceive<Action> { action ->
                    try {
                        actionService.updateAction(action)
                        call.respond(HttpStatusCode.OK)
                    } catch (e: InActiveProductException) {
                        call.respond(HttpStatusCode.BadRequest)
                    }
                }
            }

            delete("/{id}") {
                actionService.deleteActionById(call.parameters["id"]!!.toLong())
                call.respond(HttpStatusCode.OK)
            }

            authenticate("jwt-access") {
                route("/batch") {
                    post {
                        call.safetyReceive<BatchAction> { batchAction ->
                            val principal = call.principal<JWTPrincipal>()
                            call.respond(
                                actionService.createBatchActions(
                                    batchAction,
                                    principal!!.payload.getClaim("id").asLong()
                                )
                            )
                        }
                    }
                }

                post {
                    call.safetyReceive<ActionRequest> { actionRequest ->
                        val principal = call.principal<JWTPrincipal>()
                        try {
                            call.respond(
                                actionService.createAction(
                                    Action(
                                        actionRequest,
                                        principal!!.payload.getClaim("id").asLong()
                                    )
                                )
                            )
                        } catch (e: InActiveProductException) {
                            call.respond(HttpStatusCode.BadRequest)
                        }
                    }
                }
            }


            get("/full") {
                val actions = fullActionService.getAllFull()
                call.respond(actions)
            }

//            get("/task/{task_id}/full") {
//                val actions = fullActionService.getFullByTaskId(call.parameters["task_id"]!!.toInt())
//                call.respond(actions ?: HttpStatusCode.NotFound)
//            }
//
//            get("/batch/{batch_id}/full") {
//                val actions = fullActionService.getFullByBatchId(call.parameters["batch_id"]!!.toLong())
//                call.respond(actions ?: HttpStatusCode.NotFound)
//            }
//
//            get("/bobbin/{bobbin_id}/full") {
//                val actions = fullActionService.getFullByBobbinId(call.parameters["bobbin_id"]!!.toLong())
//                call.respond(actions ?: HttpStatusCode.NotFound)
//            }
//
//            get("/{id}/full") {
//                val action = fullActionService.getFullById(call.parameters["id"]!!.toLong())
//                call.respond(action ?: HttpStatusCode.NotFound)
//            }

        }
    }
}