package com.remcoil.module.batch

import com.remcoil.data.model.batch.BatchIdentity
import com.remcoil.service.batch.BatchService
import com.remcoil.service.bobbin.BobbinService
import com.remcoil.utils.safetyReceive
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.css.*
import kotlinx.html.*
import kotlinx.html.body
import kotlinx.html.img
import kotlinx.html.p
import kotlinx.html.table
import kotlinx.html.td
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

fun Application.batchModule() {
    val batchService: BatchService by closestDI().instance()
    val bobbinService: BobbinService by closestDI().instance()

    routing {
        route("/batch") {
            get {
                val batches = batchService.getAll()
                call.respond(batches)
            }

            get("/{id}") {
                val batch = batchService.getById(call.parameters["id"]!!.toLong())
                call.respond(batch ?: HttpStatusCode.BadRequest)
            }

            get("task/{task_id}") {
                val batches = batchService.getByTaskId(call.parameters["task_id"]!!.toInt())
                call.respond(batches)
            }

            post {
                call.safetyReceive<BatchIdentity> { batchIdentity ->
                    call.respond(batchService.createByIdentity(batchIdentity) ?: HttpStatusCode.BadRequest)
                }
            }

            delete("/{id}") {
                batchService.deleteBatchById(call.parameters["id"]!!.toLong())
                call.respond(HttpStatusCode.OK)
            }

            route("/full") {
                get {
                    val batches = batchService.getAllFull()
                    call.respond(batches)
                }

                get("/{id}") {
                    val batch = batchService.getFullById(call.parameters["id"]!!.toLong())
                    call.respond(batch)
                }
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
                        textAlign = TextAlign.center
                        padding(20.px, 40.px)
                        fontSize = 12.pt
                    }
                    h3 {
                        textAlign = TextAlign.center
                    }
                    p {
                        padding(5.px)
                    }
                    tr {
                        height = 150.px
                    }
                    img {
                        height = 150.px
                    }
                }
            }

            get("/codes/{batch_id}") {
                val bobbins = bobbinService.getByBatchId(call.parameters["batch_id"]!!.toLong())
                val bobbinsIterator = bobbins.iterator()
                call.respondHtml {
                    head {
                        link(rel = "stylesheet", href = "/batch/styles.css", type = "text/css")
                    }
                    body {
                        table {
                            while (bobbinsIterator.hasNext()) {
                                tr {
                                    for (i in 1..5) {
                                        if (bobbinsIterator.hasNext()) {
                                            val bobbin = bobbinsIterator.next()
                                            td {
                                                p {
                                                    +bobbin.bobbinNumber
                                                }
                                                img(src = "https://api.qrserver.com/v1/create-qr-code/?data=bobbin:${bobbin.id}")
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
}

suspend inline fun ApplicationCall.respondCss(builder: CssBuilder.() -> Unit) {
    this.respondText(CssBuilder().apply(builder).toString(), ContentType.Text.CSS)
}