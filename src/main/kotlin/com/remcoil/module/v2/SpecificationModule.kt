package com.remcoil.module.v2

import com.remcoil.data.model.v2.SpecificationPostRequest
import com.remcoil.data.model.v2.SpecificationPutRequest
import com.remcoil.service.v2.SpecificationService
import com.remcoil.utils.exceptions.EntryDoesNotExistException
import com.remcoil.utils.respondNullable
import com.remcoil.utils.safetyReceive
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
                            specificationService.getSpecificationById(it)
                        }
                    }
                    call.respondNullable(specification, onNull = HttpStatusCode.BadRequest)
                } catch (e: EntryDoesNotExistException) {
                    call.respond(HttpStatusCode.NotFound, e.message.toString())
                }
            }

            post {
                call.safetyReceive<SpecificationPostRequest> { specificationRequest ->
                    call.respond(specificationService.createSpecification(specificationRequest))
                }
            }

            put {
                call.safetyReceive<SpecificationPutRequest> { specificationRequest ->
                    call.respond(specificationService.updateSpecification(specificationRequest))
                }
            }

            delete("/{id}") {
                val result = call.parameters["id"]?.let { id ->
                    id.toLongOrNull()?.let {
                        specificationService.deleteSpecificationById(it)
                    }
                }
                call.respondNullable(result, onNull = HttpStatusCode.BadRequest)
            }

        }
    }
}
