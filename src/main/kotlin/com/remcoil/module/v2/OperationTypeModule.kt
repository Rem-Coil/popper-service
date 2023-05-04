package com.remcoil.module.v2

import com.remcoil.data.model.v2.OperationType
import com.remcoil.service.v2.OperationTypeService
import com.remcoil.utils.respondNullable
import com.remcoil.utils.safetyReceive
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

fun Application.operationTypeModuleV2() {
    val operationTypeService: OperationTypeService by closestDI().instance()

    routing {
        route("/v2/operation_type") {
            get("/specification/{id}") {
                val result = call.parameters["id"]?.let { id ->
                    id.toLongOrNull()?.let {
                        operationTypeService.getOperationTypesBySpecificationId(it)
                    }
                }
                call.respondNullable(result, HttpStatusCode.BadRequest)
            }

            put {
                call.safetyReceive<OperationType> { operationType ->
                    operationTypeService.updateOperationType(operationType)
                    call.respond(HttpStatusCode.OK)
                }
            }

            delete("/{id}") {
                val result = call.parameters["id"]?.let { id ->
                    id.toLongOrNull()?.let {
                        operationTypeService.deleteOperationTypeById(it)
                    }
                }
                call.respondNullable(result, onNull = HttpStatusCode.BadRequest)
            }

            post {
                call.safetyReceive<OperationType> { operationType ->
                    call.respond(operationTypeService.createOperationType(operationType))
                }
            }
        }
    }
}