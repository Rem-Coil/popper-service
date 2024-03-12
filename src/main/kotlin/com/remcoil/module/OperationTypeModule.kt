package com.remcoil.module

import com.remcoil.service.OperationTypeService
import com.remcoil.utils.respondNullable
import com.remcoil.utils.safetyReceive
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

fun Application.operationTypeModule() {
    val operationTypeService: OperationTypeService by closestDI().instance()

    routing {
        route("/operation_type") {
            get("/specification/{id}") {
                val result = call.parameters["id"]?.toLongOrNull()?.let {
                    operationTypeService.getOperationTypesBySpecificationId(it)
                }
                call.respondNullable(result, HttpStatusCode.BadRequest)
            }

            authenticate("admin-access") {
                put {
                    call.safetyReceive<com.remcoil.model.dto.OperationType> { operationType ->
                        operationTypeService.updateOperationType(operationType)
                        call.respond(HttpStatusCode.OK)
                    }
                }

                delete("/{id}") {
                    val result = call.parameters["id"]?.toLongOrNull()?.let {
                        operationTypeService.deleteOperationTypeById(it)
                    }
                    call.respondNullable(result, onNull = HttpStatusCode.BadRequest)
                }

                post {
                    call.safetyReceive<com.remcoil.model.dto.OperationType> { operationType ->
                        call.respond(operationTypeService.createOperationType(operationType))
                    }
                }
            }
        }
    }
}