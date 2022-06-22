package com.remcoil.module.task

import com.remcoil.data.model.task.Task
import com.remcoil.data.model.task.TaskIdentity
import com.remcoil.service.task.TaskService
import com.remcoil.utils.safetyReceive
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

fun Application.taskModule() {
    val service: TaskService by closestDI().instance()

    routing {
        route("/task") {

            get {
                val tasks = service.getAllTasks()
                call.respond(tasks)
            }

            get("/{id}") {
                val task = service.getById(call.parameters["id"]!!.toInt())
                call.respond(task ?: HttpStatusCode.BadRequest)
            }

            post {
                call.safetyReceive<TaskIdentity> { task ->
                    call.respond(service.createTask(task))
                }
            }

            put {
                call.safetyReceive<Task> { task ->
                    service.updateTask(task)
                    call.respond(HttpStatusCode.OK)
                }
            }

            delete("/{id}") {
                service.deleteTask(call.parameters["id"]!!.toInt())
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}