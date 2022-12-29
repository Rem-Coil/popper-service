package com.remcoil.module.task

import com.remcoil.data.model.task.Task
import com.remcoil.data.model.task.TaskIdentity
import com.remcoil.service.task.TaskService
import com.remcoil.utils.exceptions.EntryDoesNotExistException
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
                val bobbins = call.parameters["task_id"]?.let { id ->
                    id.toLongOrNull()?.let {
                        taskService.getBobbinsByTaskId(id.toInt())
                    }
                }
                call.respond(bobbins ?: HttpStatusCode.BadRequest)
            }

            get {
                val tasks = taskService.getAll()
                call.respond(tasks)
            }

            get("/{id}") {
                try {
                    val task = call.parameters["id"]?.let { id ->
                        id.toLongOrNull()?.let {
                            taskService.getById(id.toInt())
                        }
                    }
                    call.respond(task ?: HttpStatusCode.BadRequest)
                } catch (e: EntryDoesNotExistException) {
                    call.respond(HttpStatusCode.NotFound, e.message.toString())
                }
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
                val result = call.parameters["id"]?.let { id ->
                    id.toLongOrNull()?.let {
                        taskService.deleteTask(id.toInt())
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


            get("/full") {
                val tasks = taskService.getAllFull()
                call.respond(tasks)
            }

            get("/{id}/full") {
                try {
                    val task = call.parameters["id"]?.let { id ->
                        id.toLongOrNull()?.let {
                            taskService.getFullById(id.toInt())
                        }
                    }
                    call.respond(task ?: HttpStatusCode.BadRequest)
                } catch (e: EntryDoesNotExistException) {
                    call.respond(HttpStatusCode.NotFound, e.message.toString())
                }
            }

        }
    }
}