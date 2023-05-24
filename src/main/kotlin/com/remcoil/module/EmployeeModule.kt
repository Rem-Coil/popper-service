package com.remcoil.module

import com.remcoil.service.EmployeeService
import com.remcoil.utils.respondNullable
import com.remcoil.utils.safeLet
import com.remcoil.utils.safetyReceive
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

fun Application.employeeModule() {
    val employeeService: EmployeeService by closestDI().instance()

    routing {
        route("/employee") {

            get {
                val operators = employeeService.getAllEmployees(call.request.queryParameters["active_only"].toBoolean())
                call.respond(operators)
            }

            post("/sign_in") {
                call.safetyReceive<com.remcoil.model.dto.EmployeeCredentials> { credentials ->
                    val token = employeeService.getEmployeeByCredentials(credentials)
                    call.respond(hashMapOf("token" to token))
                }
            }

            post("/sign_up") {
                call.safetyReceive<com.remcoil.model.dto.Employee> { employee ->
                    val token = employeeService.createEmployee(employee)
                    call.respond(hashMapOf("token" to token))
                }
            }

            put {
                call.safetyReceive<com.remcoil.model.dto.Employee> { employee ->
                    employeeService.updateEmployee(employee)
                    call.respond(HttpStatusCode.OK)
                }
            }

            put("/{id}/state") {
                val result = safeLet(
                    call.parameters["id"]?.toLongOrNull(),
                    call.parameters["active"]?.toBooleanStrictOrNull()
                ) { id, active ->
                    employeeService.updateEmployeeState(id, active)
                }
                call.respondNullable(result, HttpStatusCode.BadRequest)
            }

            delete("/{id}") {
                val result = call.parameters["id"]?.toLongOrNull()?.let {
                    employeeService.deleteEmployeeById(it)
                }
                call.respondNullable(result, onNull = HttpStatusCode.BadRequest)
            }
        }
    }
}