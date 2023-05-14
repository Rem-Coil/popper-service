package com.remcoil.module.v2

import com.remcoil.service.v2.ProductService
import com.remcoil.utils.exceptions.EntryDoesNotExistException
import com.remcoil.utils.respondNullable
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

fun Application.productModuleV2() {
    val productService: ProductService by closestDI().instance()

    routing {
        get("/v2/batch/{id}/product") {
            val products = call.parameters["id"]?.let { id ->
                id.toLongOrNull()?.let { productService.getProductsByBatchId(it) }
            }
            call.respondNullable(products, onNull = HttpStatusCode.BadRequest)
        }

        route("/v2/product") {
            get {
                val products = productService.getAllProducts()
                call.respond(products)
            }

            get("/{id}") {
                try {
                    val product = call.parameters["id"]?.toLongOrNull()?.let {
                        productService.getProductById(it)
                    }
                    call.respondNullable(product, onNull = HttpStatusCode.BadRequest)
                } catch (e: EntryDoesNotExistException) {
                    call.respond(HttpStatusCode.NotFound, e.message.toString())
                }
            }

            patch("/{id}/deactivate") {
                try {
                    val result = call.parameters["id"]?.toLongOrNull()?.let {
                        productService.deactivateProductById(it)
                    }
                    call.respondNullable(result, onNull = HttpStatusCode.BadRequest)
                } catch (e: EntryDoesNotExistException) {
                    call.respond(HttpStatusCode.NotFound, e.message.toString())
                }
            }
        }
    }
}