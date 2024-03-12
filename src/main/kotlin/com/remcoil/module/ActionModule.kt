package com.remcoil.module

import com.remcoil.model.dto.Action
import com.remcoil.model.dto.ActionRequest
import com.remcoil.model.dto.BatchActionRequest
import com.remcoil.service.ActionService
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

fun Application.actionModule() {
    val actionService: ActionService by closestDI().instance()

    routing {
        route("/action") {
            get {
                val actions = actionService.getAllActions()
                call.respond(actions)
            }


            get("/product/{id}") {
                val actions = call.parameters["id"]?.toLongOrNull()?.let {
                    actionService.getActionsByProductId(it)
                }
                call.respondNullable(actions, onNull = HttpStatusCode.BadRequest)
            }

            authenticate("employee-access") {
                put {
                    call.safetyReceive<Action> { action ->
                        actionService.updateAction(action)
                        call.respond(HttpStatusCode.OK)
                    }
                }

                delete("/{id}") {
                    val result = call.parameters["id"]?.toLongOrNull()?.let {
                        actionService.deleteActionById(it)
                    }
                    call.respondNullable(result, onNull = HttpStatusCode.BadRequest)
                }

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
                        call.respond(
                            actionService.createAction(
                                actionRequest.toAction(
                                    principal!!.payload.getClaim("id").asLong()
                                )
                            )
                        )
                    }
                }
            }

        }
    }
}