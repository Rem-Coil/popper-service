package com.remcoil.module

import com.remcoil.model.dto.AcceptanceAction
import com.remcoil.model.dto.AcceptanceActionRequest
import com.remcoil.service.AcceptanceActionService
import com.remcoil.utils.exceptions.EntryDoesNotExistException
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

fun Application.acceptanceActionModule() {
    val acceptanceActionService: AcceptanceActionService by closestDI().instance()

    routing {
        route("/acceptance_action") {
            get {
                val actions = acceptanceActionService.getAll()
                call.respond(actions)
            }

            get("/product/{id}") {
                try {
                    val actions = call.parameters["id"]?.toLongOrNull()?.let {
                        acceptanceActionService.getByProductId(it)
                    }
                    call.respondNullable(actions, onNull = HttpStatusCode.BadRequest)
                } catch (e: EntryDoesNotExistException) {
                    call.respond(HttpStatusCode.NotFound, e.message.toString())
                }
            }

            authenticate("quality-engineer-access") {
                post {
                    call.safetyReceive<AcceptanceActionRequest> {
                        val principal = call.principal<JWTPrincipal>()
                        call.respond(
                            acceptanceActionService.batchCreate(
                                it.toAcceptanceActions(
                                    principal!!.payload.getClaim("id").asLong()
                                )
                            )
                        )
                    }
                }
                put {
                    call.safetyReceive<AcceptanceAction> {
                        call.respond(acceptanceActionService.update(it))
                    }
                }
                delete("/{id}") {
                    val result = call.parameters["id"]?.toLongOrNull()?.let {
                        acceptanceActionService.deleteById(it)
                    }
                    call.respondNullable(result, onNull = HttpStatusCode.BadRequest)
                }
            }
        }
    }
}