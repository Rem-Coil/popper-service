package com.remcoil.module.v2

import com.remcoil.data.model.v2.ControlAction
import com.remcoil.data.model.v2.ControlActionRequest
import com.remcoil.service.v2.ControlActionService
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

fun Application.controlActionModuleV2() {
    val controlActionService: ControlActionService by closestDI().instance()

    routing {
        route("/v2/control_action") {
            get("/product/{id}") {
                val controlActions = call.parameters["id"]?.toLongOrNull()?.let {
                    controlActionService.getControlActionsByProductId(it)
                }
                call.respondNullable(controlActions, onNull = HttpStatusCode.BadRequest)
            }

            get {
                val controlActions = controlActionService.getAllControlActions()
                call.respond(controlActions)
            }


            put {
                call.safetyReceive<ControlAction> { controlAction ->
                    controlActionService.updateControlAction(controlAction)
                    call.respond(HttpStatusCode.OK)
                }
            }

            delete("/{id}") {
                val result = call.parameters["id"]?.toLongOrNull()?.let {
                    controlActionService.deleteControlAction(it)
                }
                call.respondNullable(result, onNull = HttpStatusCode.BadRequest)
            }

            authenticate("jwt-access") {
                post {
                    call.safetyReceive<ControlActionRequest> { controlActionRequest ->
                        val principal = call.principal<JWTPrincipal>()
                        try {
                            call.respond(
                                controlActionService.createControlAction(
                                    controlActionRequest.toControlAction(
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