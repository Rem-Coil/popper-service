package com.remcoil.module.operator

import com.remcoil.data.model.operator.Operator
import com.remcoil.service.operator.OperatorService
import com.remcoil.utils.safetyReceive
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.kodein.di.*
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
                val token = service.getOperator(call.receive())
                if (token == null) {
                    call.respond(HttpStatusCode.Unauthorized)
                } else {
                    call.respond(hashMapOf("token" to token))
                }
            }

            post("/sign_up") {
                val token = service.createOperator(call.receive())
                if (token != null) {
                    call.respond(hashMapOf("token" to token))
                } else {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }

            put {
                call.safetyReceive<Operator> { operator ->
                    service.updateOperator(operator)
                    call.respond(HttpStatusCode.OK)
                }
            }

            delete("/{id}") {
                service.deleteOperator(call.parameters["id"]!!.toInt())
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}