package com.remcoil.module.v2

import com.remcoil.data.model.v2.Specification
import com.remcoil.service.v2.SpecificationService
import com.remcoil.utils.exceptions.EntryDoesNotExistException
import com.remcoil.utils.safetyReceive
import com.remcoil.utils.safetyRespond
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

fun Application.specificationModuleV2() {
    val specificationService: SpecificationService by closestDI().instance()

    routing {
        route("/v2/specification") {
            get {
                val specifications = specificationService.getAllSpecifications()
                call.respond(specifications)
            }

            get("/{id}") {
                try {
                    val specification = call.parameters["id"]?.let { id ->
                        id.toLongOrNull()?.let {
                            specificationService.getSpecificationById(id.toLong())
                        }
                    }
                    call.safetyRespond(specification, HttpStatusCode.BadRequest)
                } catch (e: EntryDoesNotExistException) {
                    call.respond(HttpStatusCode.NotFound, e.message.toString())
                }
            }

            post {
                call.safetyReceive<Specification> { specification ->
                    call.respond(specificationService.createSpecification(specification))
                }
            }

            put {
                call.safetyReceive<Specification> { specification ->
                    call.respond(specificationService.updateSpecification(specification))
                }
            }

            delete("/{id}") {
                val result = call.parameters["id"]?.let { id ->
                    id.toLongOrNull()?.let {
                        specificationService.deleteSpecificationById(id.toLong())
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
