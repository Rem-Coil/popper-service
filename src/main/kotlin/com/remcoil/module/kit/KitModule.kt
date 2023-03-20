package com.remcoil.module.kit

import com.remcoil.data.model.kit.Kit
import com.remcoil.service.batch.BatchService
import com.remcoil.service.kit.KitService
import com.remcoil.utils.exceptions.EntryDoesNotExistException
import com.remcoil.utils.safetyReceive
import com.remcoil.utils.safetyRespond
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

fun Application.kitModule() {
    val kitService: KitService by closestDI().instance()
    val batchService: BatchService by closestDI().instance()

    routing {
        route("/kit") {

            get("/{kit_id}/product") {
                val products = call.parameters["kit_id"]?.let { id ->
                    id.toLongOrNull()?.let {
                        kitService.getProductsByKitId(id.toLong())
                    }
                }
                call.safetyRespond(products, HttpStatusCode.BadRequest)
            }

            get("/{kit_id}/batches") {
                val batches = call.parameters["kit_id"]?.let {kitId ->
                    kitId.toLongOrNull()?.let {
                        batchService.getBatchesByKitId(kitId.toLong())
                    }
                }
                call.safetyRespond(batches, HttpStatusCode.BadRequest)
            }

            get {
                val kits = kitService.getAllKits()
                call.respond(kits)
            }

            get("/{id}") {
                try {
                    val kit = call.parameters["id"]?.let { id ->
                        id.toLongOrNull()?.let {
                            kitService.getKitById(id.toLong())
                        }
                    }
                    call.safetyRespond(kit, HttpStatusCode.BadRequest)
                } catch (e: EntryDoesNotExistException) {
                    call.respond(HttpStatusCode.NotFound, e.message.toString())
                }
            }

            post {
                call.safetyReceive<Kit> { kit ->
                    call.respond(kitService.createKit(kit))
                }
            }

            put {
                call.safetyReceive<Kit> { kit ->
                    kitService.updateKit(kit)
                    call.respond(HttpStatusCode.OK)
                }
            }

            delete("/{id}") {
                val result = call.parameters["id"]?.let { id ->
                    id.toLongOrNull()?.let {
                        kitService.deleteKitById(id.toLong())
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


//            get("/full") {
//                val tasks = specificationService.getAllFull()
//                call.respond(tasks)
//            }
//
//            get("/{id}/full") {
//                try {
//                    val task = call.parameters["id"]?.let { id ->
//                        id.toLongOrNull()?.let {
//                            specificationService.getFullById(id.toInt())
//                        }
//                    }
//                    call.respond(task ?: HttpStatusCode.BadRequest)
//                } catch (e: EntryDoesNotExistException) {
//                    call.respond(HttpStatusCode.NotFound, e.message.toString())
//                }
//            }

        }
    }
}