package com.remcoil.module.v2

import com.remcoil.service.v2.ProductService
import com.remcoil.utils.exceptions.EntryDoesNotExistException
import com.remcoil.utils.safetyRespond
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

fun Application.productModuleV2() {
    val productService: ProductService by closestDI().instance()

    routing {

        get("/v2/batch/{batch_id}/product") {
            val products = call.parameters["batch_id"]?.let { id ->
                id.toLongOrNull()?.let {
                    productService.getProductsByBatchId(id.toLong())
                }
            }
            call.safetyRespond(products, HttpStatusCode.BadRequest)
        }

        route("/v2/product") {
            get {
                val products = productService.getAllProducts()
                call.respond(products)
            }

            delete("/{id}") {
                try {
                    val result = call.parameters["id"]?.let { id ->
                        id.toLongOrNull()?.let {
                            productService.deactivateProductById(id.toLong())
                        }
                    }
                    call.safetyRespond(result, HttpStatusCode.BadRequest)
                } catch (e: EntryDoesNotExistException) {
                    call.respond(HttpStatusCode.NotFound, e.message.toString())
                }
            }

            delete("{id}/delete") {
                val result = call.parameters["id"]?.let { id ->
                    id.toLongOrNull()?.let {
                        productService.deleteProductById(id.toLong())
                    }
                }
                call.safetyRespond(result, HttpStatusCode.BadRequest)
            }
        }
    }
}