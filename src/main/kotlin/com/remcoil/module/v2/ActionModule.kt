package com.remcoil.module.v2

import com.remcoil.data.model.v2.Action
import com.remcoil.data.model.v2.ActionRequest
import com.remcoil.data.model.v2.BatchActionRequest
import com.remcoil.service.v2.ActionService
import com.remcoil.utils.exceptions.InActiveProductException
import com.remcoil.utils.respondNullable
import com.remcoil.utils.safetyReceive
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

fun Application.actionModuleV2() {
    val actionService: ActionService by closestDI().instance()

    routing {
        route("/v2/action") {
            get {
                val actions = actionService.getAllActions()
                call.respond(actions)
            }


            get("/product/{id}") {
                val actions = call.parameters["id"]?.let { productId ->
                    productId.toLongOrNull()?.let {
                        actionService.getActionsByProductId(it)
                    }
                }
                call.respondNullable(actions, onNull = HttpStatusCode.BadRequest)
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
                        call.safetyReceive<BatchActionRequest> { batchActionRequest ->
                            val principal = call.principal<JWTPrincipal>()
                            call.respond(
                                actionService.createBatchActions(
                                    batchActionRequest,
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
                                    actionRequest.toAction(
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

        }
    }
}