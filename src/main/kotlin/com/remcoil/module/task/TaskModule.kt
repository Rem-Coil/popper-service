package com.remcoil.module.task

import com.remcoil.service.task.TaskService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
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
                call.respond(task ?: HttpStatusCode.NotFound)
            }

            post {
                val task = service.createTask(call.receive())
                call.respond(task)
            }

            delete("/{id}") {
                service.deleteTask(call.parameters["id"]!!.toInt())
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}