package com.remcoil.module

import com.remcoil.service.ControlActionService
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

fun Application.controlActionModule() {
    val controlActionService: ControlActionService by closestDI().instance()

    routing {
        route("/control_action") {
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

            authenticate("quality-engineer-access") {
                post {
                    call.safetyReceive<com.remcoil.model.dto.ControlActionRequest> { controlActionRequest ->
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

                put {
                    call.safetyReceive<com.remcoil.model.dto.ControlAction> { controlAction ->
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
            }

        }
    }
}