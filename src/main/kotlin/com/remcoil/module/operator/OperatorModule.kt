package com.remcoil.module.operator

import com.remcoil.data.model.operator.Operator
import com.remcoil.data.model.operator.OperatorCredentials
import com.remcoil.service.operator.OperatorService
import com.remcoil.utils.exceptions.WrongParamException
import com.remcoil.utils.safetyReceive
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

fun Application.operatorModule() {
    val service: OperatorService by closestDI().instance()

    routing {
        route("/operator") {

            get {
                val operators = service.getAllOperators(call.request.queryParameters["active_only"].toBoolean())
                call.respond(operators)
            }

            post("/sign_in") {
                call.safetyReceive<OperatorCredentials> { operatorCredentials ->
                    val token = service.getActiveOperator(operatorCredentials)
                    if (token == null) {
                        call.respond(HttpStatusCode.Unauthorized)
                    } else {
                        call.respond(hashMapOf("token" to token))
                    }
                }
            }

            post("/sign_up") {
                call.safetyReceive<Operator> { operator ->
                    val token = service.createOperator(operator)
                    if (token != null) {
                        call.respond(hashMapOf("token" to token))
                    } else {
                        call.respond(HttpStatusCode.BadRequest)
                    }
                }
            }

            put {
                call.safetyReceive<Operator> { operator ->
                    try {
                        service.updateOperator(operator)
                        call.respond(HttpStatusCode.OK)
                    } catch (e: WrongParamException) {
                        call.respond(HttpStatusCode.BadRequest)
                    }
                }
            }

            put("state/{id}") {
                try {
                    service.setOperatorState(
                        call.parameters["id"]!!.toInt(),
                        call.request.queryParameters["active"].toBoolean()
                    )
                    call.respond(HttpStatusCode.OK)
                } catch (e: NotFoundException) {
                    call.respond(HttpStatusCode.NotFound)
                }
            }

            delete("/{id}") {
                service.deleteOperator(call.parameters["id"]!!.toInt())
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}