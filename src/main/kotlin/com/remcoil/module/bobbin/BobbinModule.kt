package com.remcoil.module.bobbin

import com.remcoil.service.bobbin.BobbinService
import com.remcoil.service.task.TaskService
import io.ktor.application.*
import io.ktor.html.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.css.*
import kotlinx.html.*
import org.kodein.di.*
import org.kodein.di.ktor.closestDI

fun Application.bobbinModule() {
    val bobbinService: BobbinService by closestDI().instance()
    val taskService: TaskService by closestDI().instance()

    routing {
        route("/bobbin") {
            get {
                val bobbins = bobbinService.getAll()
                call.respond(bobbins)
            }

            get("/{id}") {
                val bobbin = bobbinService.getById(call.parameters["id"]!!.toInt())
                call.respond(bobbin ?: HttpStatusCode.BadRequest)
            }

            get("/task/{id}") {
                val bobbins = bobbinService.getByTask(call.parameters["id"]!!.toInt())
                call.respond(bobbins)
            }

            get("/styles.css") {
                call.respondCss {
                    body {
                        fontFamily = "Verdana"
                    }
                    table {
                        margin(0.px, LinearDimension.auto)
                    }
                    td {
                        margin(10.px)
                        fontSize = 12.pt
                    }
                    h3 {
                        textAlign = TextAlign.center
                    }
                    p {
                        paddingLeft = 20.px
                    }
                    tr {
                        height = 150.px
                    }
                }
            }


            get("/codes/{id}") {
                val bobbins = bobbinService.getByTask(call.parameters["id"]!!.toInt())
                val task = taskService.getById(call.parameters["id"]!!.toInt())
                call.respondHtml {
                    head {
                        link(rel = "stylesheet", href = "/bobbin/styles.css", type = "text/css")
                    }
                    body {
                        table {
                            tr {
                                td {
                                    h3 {
                                        +"ТЗ: ${task?.taskName} ${task?.taskNumber}"
                                    }
                                }
                            }
                            for (bobbin in bobbins) {
                                tr {
                                    td {
                                        p {
                                            +"Номер катушки: ${bobbin.bobbinNumber}"
                                        }
                                        p {
                                            +"ID катушки: ${bobbin.id}"
                                        }
                                        p {
                                            +"ID тз: ${bobbin.taskId}"
                                        }
                                    }
                                    td {
                                        a(href = "http://qrcoder.ru") {
                                            img(src = "http://qrcoder.ru/code/?bobbin%3A${bobbin.id}&5&0")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }
    }
}

suspend inline fun ApplicationCall.respondCss(builder: CssBuilder.() -> Unit) {
    this.respondText(CssBuilder().apply(builder).toString(), ContentType.Text.CSS)
}