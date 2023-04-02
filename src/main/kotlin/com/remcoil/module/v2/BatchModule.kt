package com.remcoil.module.v2

import com.remcoil.service.v2.BatchService
import com.remcoil.service.v2.ProductService
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
                                        +batch.batchNumber
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
                                                    +product.productNumber
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