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


            get("/full") {
                val batches = batchService.getAllFull()
                call.respond(batches)
            }

            get("/{id}/full") {
                val batch = batchService.getFullById(call.parameters["id"]!!.toLong())
                call.respond(batch)
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
                        width = 200.px
                        textAlign = TextAlign.center
                        padding(20.px, 20.px)
                        fontSize = 12.pt
                    }
                    h3 {
                        textAlign = TextAlign.center
                    }
                    p {
                        margin(0.px)
                    }
                    tr {
                        height = 150.px
                    }
                    img {
                        marginTop = 10.px
                        height = 150.px
                    }
                }
            }

            get("/{id}/codes") {
                val batchId = call.parameters["id"]!!.toLong()
                val batch = batchService.getById(batchId)
                if (batch == null) {
                    call.respondHtml { HttpStatusCode.NoContent }
                    return@get
                }
                val bobbins = bobbinService.getByBatchId(batchId)
                val bobbinsIterator = bobbins.iterator()
                call.respondHtml {
                    head {
                        link(rel = "stylesheet", href = "/batch/styles.css", type = "text/css")
                    }
                    body {
                        table {
                            tr {
                                td {
                                    p {
                                        +"Партия"
                                    }
                                    p {
                                        +batch.batchNumber
                                    }
                                    img(src = "https://api.qrserver.com/v1/create-qr-code/?data=batch:${batch.id}")
                                }
                            }
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

            get("/example/styles.css") {
                call.respondCss {
                    body {
                        fontFamily = "Verdana"
                    }
                    td {
                        textAlign = TextAlign.center
                        padding(10.px, 10.px)
                        fontSize = 9.pt
                    }
                    h3 {
                        textAlign = TextAlign.center
                    }
                    p {
                        margin(0.px)
                    }
                    img {
                        marginTop = 10.px
                    }
                }
            }
            get("/codes/example") {
                call.respondHtml {
                    head {
                        link(rel = "stylesheet", href = "/batch/example/styles.css", type = "text/css")
                    }
                    body {
                        table {
                            tr {
                                for (i in 1..5) {
                                    td {
                                        p {
                                            +"Номер катушки"
                                        }
                                        img(src = "https://api.qrserver.com/v1/create-qr-code/?size=${250}x${250}&data=bobbin:${i}")
                                    }

                                }

                            }
                        }
                        table {
                            tr {
                                for (i in 1..6) {
                                    td {
                                        p {
                                            +"Номер катушки"
                                        }
                                        img(src = "https://api.qrserver.com/v1/create-qr-code/?size=${205}x${205}&data=bobbin:${i}")
                                    }

                                }

                            }
                        }
                        table {
                            tr {
                                for (i in 1..7) {
                                    td {
                                        p {
                                            +"Номер катушки"
                                        }
                                        img(src = "https://api.qrserver.com/v1/create-qr-code/?size=${173}x${173}&data=bobbin:${i}")
                                    }

                                }

                            }
                        }
                        table {
                            tr {
                                for (i in 1..8) {
                                    td {
                                        p {
                                            +"Номер катушки"
                                        }
                                        img(src = "https://api.qrserver.com/v1/create-qr-code/?size=${149}x${149}&data=bobbin:${i}")
                                    }

                                }

                            }
                        }
                        table {
                            tr {
                                for (i in 1..9) {
                                    td {
                                        p {
                                            +"Номер катушки"
                                        }
                                        img(src = "https://api.qrserver.com/v1/create-qr-code/?size=${(130)}x${130}&data=bobbin:${i}")
                                    }

                                }

                            }
                        }
                        table {
                            tr {
                                for (i in 1..10) {
                                    td {
                                        p {
                                            +"Номер катушки"
                                        }
                                        img(src = "https://api.qrserver.com/v1/create-qr-code/?size=${115}x${115}&data=bobbin:${i}")
                                    }

                                }

                            }
                        }
                        table {
                            tr {
                                for (i in 1..11) {
                                    td {
                                        p {
                                            +"Номер катушки"
                                        }
                                        img(src = "https://api.qrserver.com/v1/create-qr-code/?size=${102}x${102}&data=bobbin:${i}")
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