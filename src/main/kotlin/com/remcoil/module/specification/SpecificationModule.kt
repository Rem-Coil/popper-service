package com.remcoil.module.specification

import com.remcoil.data.model.specification.Specification
import com.remcoil.service.specification.SpecificationService
import com.remcoil.utils.exceptions.EntryDoesNotExistException
import com.remcoil.utils.safetyReceive
import com.remcoil.utils.safetyRespond
import io.ktor.http.*
import io.ktor.server.application.*
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
                    specificationService.updateSpecification(specification)
                    call.respond(HttpStatusCode.OK)
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


//            get("/full") {
//                val tasks = specificationService.getAllFull()
//                call.respond(tasks)
//            }
//
//            get("/{id}/full") {
//                try {
//                    val task = call.parameters["id"]?.let { id ->
//                        id.toLongOrNull()?.let {
//                            specificationService.getFullById(id.toInt())
//                        }
//                    }
//                    call.respond(task ?: HttpStatusCode.BadRequest)
//                } catch (e: EntryDoesNotExistException) {
//                    call.respond(HttpStatusCode.NotFound, e.message.toString())
//                }
//            }

        }
    }
}