package com.remcoil.module

import com.remcoil.model.dto.BatchControlActionRequest
import com.remcoil.model.dto.ControlAction
import com.remcoil.model.dto.ControlActionRequest
import com.remcoil.service.ControlActionService
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
                    controlActionService.getByProductId(it)
                }
                call.respondNullable(controlActions, onNull = HttpStatusCode.BadRequest)
            }

            get {
                val controlActions = controlActionService.getAll()
                call.respond(controlActions)
            }

            authenticate("quality-engineer-access") {
                post("/batch") {
                    call.safetyReceive<BatchControlActionRequest> { batchControlActionRequest ->
                        val principal = call.principal<JWTPrincipal>()
                        call.respond(
                            controlActionService.batchCreate(
                                batchControlActionRequest.toControlActions(
                                    principal!!.payload.getClaim("id").asLong()
                                )
                            )
                        )
                    }
                }

                post {
                    call.safetyReceive<ControlActionRequest> { controlActionRequest ->
                        val principal = call.principal<JWTPrincipal>()
                        call.respond(
                            controlActionService.create(
                                controlActionRequest.toControlAction(
                                    principal!!.payload.getClaim("id").asLong()
                                )
                            )
                        )
                    }
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
            }

        }
    }
}