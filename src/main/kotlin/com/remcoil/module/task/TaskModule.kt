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
    val taskService: TaskService by closestDI().instance()

    routing {
        route("/task") {

            get("/bobbins/{task_id}") {
                val bobbins = taskService.getBobbinsByTaskId(call.parameters["task_id"]!!.toInt())
                call.respond(bobbins)
            }

            get {
                val tasks = taskService.getAll()
                call.respond(tasks)
            }

            get("/{id}") {
                val task = taskService.getById(call.parameters["id"]!!.toInt())
                call.respond(task ?: HttpStatusCode.BadRequest)
            }

            post {
                call.safetyReceive<TaskIdentity> { task ->
                    call.respond(taskService.createTask(task))
                }
            }

            put {
                call.safetyReceive<Task> { task ->
                    taskService.updateTask(task)
                    call.respond(HttpStatusCode.OK)
                }
            }

            delete("/{id}") {
                taskService.deleteTask(call.parameters["id"]!!.toInt())
                call.respond(HttpStatusCode.OK)
            }

            route("/full") {
                get {
                    val tasks = taskService.getAllFull()
                    call.respond(tasks)
                }

                get("/{id}") {
                    val task = taskService.getFullById(call.parameters["id"]!!.toInt())
                    call.respond(task)
                }
            }
        }
    }
}