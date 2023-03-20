package com.remcoil.module.product

import com.remcoil.data.model.product.Product
import com.remcoil.service.product.ProductService
import com.remcoil.utils.exceptions.EntryDoesNotExistException
import com.remcoil.utils.safetyReceive
import com.remcoil.utils.safetyRespond
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.kodein.di.instance

import org.kodein.di.ktor.closestDI

fun Application.productModule() {
    val productService: ProductService by closestDI().instance()

    routing {

        get("/batch/{batch_id}/product") {
            val products = call.parameters["batch_id"]?.let { id ->
                id.toLongOrNull()?.let {
                    productService.getProductsByBatchId(id.toLong())
                }
            }
            call.safetyRespond(products, HttpStatusCode.BadRequest)
        }

        route("/product") {
            get {
                val products = productService.getAllProducts()
                call.respond(products)
            }

            get("/{id}") {
                try {
                    val product: Product? = call.parameters["id"]?.let { id ->
                        id.toLongOrNull()?.let {
                            productService.getProductById(id.toLong())
                        }
                    }
                    call.safetyRespond(product, HttpStatusCode.BadRequest)
                } catch (e: EntryDoesNotExistException) {
                    call.respond(HttpStatusCode.NotFound, e.message.toString())
                }
            }

            post {
                call.safetyReceive<Product> { product ->
                    val createdProduct = productService.createProduct(product)
                    call.respond(createdProduct)
                }
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


