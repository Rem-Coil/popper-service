package com.remcoil.module.v2

import com.remcoil.service.v2.BatchService
import com.remcoil.service.v2.ProductService
import com.remcoil.utils.exceptions.EntryDoesNotExistException
import com.remcoil.utils.respondNullable
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.css.*
import kotlinx.html.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

fun Application.batchModuleV2() {
    val batchService: BatchService by closestDI().instance()
    val productService: ProductService by closestDI().instance()

    routing {
        route("/v2/batch") {
            get {
                val batches = batchService.getAllBatches()
                call.respond(batches)
            }

            get("/{id}") {
                try {
                    val batch = call.parameters["id"]?.let {
                        it.toLongOrNull()?.let { id ->
                            batchService.getBatchById(id)
                        }
                    }
                    call.respondNullable(batch, onNull = HttpStatusCode.BadRequest)
                } catch (e: EntryDoesNotExistException) {
                    call.respond(HttpStatusCode.NotFound, e.message.toString())
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
                        padding(15.px, 15.px)
                        fontSize = 12.pt
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

            get("/{id}/codes") {
                val batchId = call.parameters["id"]!!.toLong()
                val batch = batchService.getBatchById(batchId)

                val products = productService.getProductsByBatchId(batchId)
                val productIterator = products.iterator()
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
                                        +batch.batchNumber.toString()
                                    }
                                    img(src = "https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=batch:${batch.id}")
                                }
                            }
                            while (productIterator.hasNext()) {
                                tr {
                                    for (i in 1..8) {
                                        if (productIterator.hasNext()) {
                                            val product = productIterator.next()
                                            td {
                                                p {
                                                    +product.productNumber.toString()
                                                }
                                                img(src = "https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=product:${product.id}")
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