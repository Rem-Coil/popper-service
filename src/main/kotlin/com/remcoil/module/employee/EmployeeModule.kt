package com.remcoil.module.employee

import com.remcoil.data.model.employee.Employee
import com.remcoil.data.model.employee.EmployeeCredentials
import com.remcoil.service.employee.EmployeeService
import com.remcoil.utils.exceptions.WrongParamException
import com.remcoil.utils.safetyReceive
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

fun Application.employeeModule() {
    val service: EmployeeService by closestDI().instance()

    routing {
        route("/employee") {

            get {
                val employees = service.getAllEmployees(call.request.queryParameters["active_only"].toBoolean())
                call.respond(employees)
            }

            post("/sign_in") {
                call.safetyReceive<EmployeeCredentials> { employeeCredentials ->
                    val token = service.getActiveEmployee(employeeCredentials)
                    if (token == null) {
                        call.respond(HttpStatusCode.Unauthorized)
                    } else {
                        call.respond(hashMapOf("token" to token))
                    }
                }
            }

            post("/sign_up") {
                call.safetyReceive<Employee> { employee ->
                    val token = service.createEmployee(employee)
                    if (token != null) {
                        call.respond(hashMapOf("token" to token))
                    } else {
                        call.respond(HttpStatusCode.BadRequest)
                    }
                }
            }

            put {
                call.safetyReceive<Employee> { employee ->
                    try {
                        service.updateEmployee(employee)
                        call.respond(HttpStatusCode.OK)
                    } catch (e: WrongParamException) {
                        call.respond(HttpStatusCode.BadRequest)
                    }
                }
            }

            put("state/{id}") {
                try {
                    service.setEmployeeState(
                        call.parameters["id"]!!.toLong(),
                        call.request.queryParameters["active"].toBoolean()
                    )
                    call.respond(HttpStatusCode.OK)
                } catch (e: NotFoundException) {
                    call.respond(HttpStatusCode.NotFound)
                }
            }

            delete("/{id}") {
                service.deleteEmployee(call.parameters["id"]!!.toLong())
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}