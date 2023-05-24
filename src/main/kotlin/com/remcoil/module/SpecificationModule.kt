package com.remcoil.module

import com.remcoil.service.SpecificationService
import com.remcoil.utils.exceptions.EntryDoesNotExistException
import com.remcoil.utils.respondNullable
import com.remcoil.utils.safetyReceive
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

fun Application.specificationModule() {
    val specificationService: SpecificationService by closestDI().instance()

    routing {
        route("/specification") {
            get {
                val specifications = specificationService.getAllSpecifications()
                call.respond(specifications)
            }

            get("/{id}") {
                try {
                    val specification = call.parameters["id"]?.toLongOrNull()?.let {
                        specificationService.getSpecificationById(it)
                    }
                    call.respondNullable(specification, onNull = HttpStatusCode.BadRequest)
                } catch (e: EntryDoesNotExistException) {
                    call.respond(HttpStatusCode.NotFound, e.message.toString())
                }
            }

            authenticate("admin-access") {
                post {
                    call.safetyReceive<com.remcoil.model.dto.SpecificationPostRequest> { specificationRequest ->
                        call.respond(specificationService.createSpecification(specificationRequest))
                    }
                }

                put {
                    call.safetyReceive<com.remcoil.model.dto.SpecificationPutRequest> { specificationRequest ->
                        call.respond(specificationService.updateSpecification(specificationRequest))
                    }
                }

                delete("/{id}") {
                    val result = call.parameters["id"]?.toLongOrNull()?.let {
                        specificationService.deleteSpecificationById(it)
                    }
                    call.respondNullable(result, onNull = HttpStatusCode.BadRequest)
                }
            }
        }
    }
}
