package com.remcoil.module.specification

import com.remcoil.data.model.specification.SpecificationAction
import com.remcoil.service.specification.SpecificationActionService
import com.remcoil.utils.exceptions.EntryDoesNotExistException
import com.remcoil.utils.safetyReceive
import com.remcoil.utils.safetyRespond
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

fun Application.specificationActionModule() {
    val specificationActionService: SpecificationActionService by closestDI().instance()

    routing {
        route("/specification/action_type") {
            get {
                val specificationActions = specificationActionService.getAllSpecificationActions()
                call.respond(specificationActions)
            }

            get("/{id}") {
                try {
                    val specificationAction = call.parameters["id"]?.let { id ->
                        id.toLongOrNull()?.let {
                            specificationActionService.getSpecificationActionById(id.toLong())
                        }
                    }
                    call.safetyRespond(specificationAction, HttpStatusCode.BadRequest)
                } catch (e: EntryDoesNotExistException) {
                    call.respond(HttpStatusCode.NotFound, e.message.toString())
                }
            }

            post {
                call.safetyReceive<SpecificationAction> { specificationAction ->
                    call.respond(specificationActionService.createSpecificationAction(specificationAction))
                }
            }

            put {
                call.safetyReceive<SpecificationAction> { specificationAction ->
                    specificationActionService.updateSpecificationAction(specificationAction)
                    call.respond(specificationAction)
                }
            }

            delete("/{id}") {
                val result = call.parameters["id"]?.let { id ->
                    id.toLongOrNull()?.let {
                        specificationActionService.deleteSpecificationActionById(id.toLong())
                    }
                }
                call.respond(
                    if (result == null) {
                        HttpStatusCode.BadRequest
                    } else {
                        HttpStatusCode.OK
                    }
                )
            }
        }
    }
}
