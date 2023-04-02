package com.remcoil.module.v2

import com.remcoil.data.model.v2.Kit
import com.remcoil.service.v2.BatchService
import com.remcoil.service.v2.KitService
import com.remcoil.utils.exceptions.EntryDoesNotExistException
import com.remcoil.utils.safetyReceive
import com.remcoil.utils.safetyRespond
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

fun Application.kitModuleV2() {
    val kitService: KitService by closestDI().instance()
    val batchService: BatchService by closestDI().instance()

    routing {
        get("/v2/specification/{specification_id}/kit") {
            val kits = call.parameters["specification_id"]?.let {id ->
                id.toLongOrNull()?.let {
                    kitService.getKitsBySpecificationId(id.toLong())
                }
            }
            call.safetyRespond(kits, HttpStatusCode.BadRequest)
        }

        route("/v2/kit") {
            get {
                val kits = kitService.getAllKits()
                call.respond(kits)
            }

            get("/{kit_id}/product") {
                val products = call.parameters["kit_id"]?.let { id ->
                    id.toLongOrNull()?.let {
                        kitService.getProductsByKitId(id.toLong())
                    }
                }
                call.safetyRespond(products, HttpStatusCode.BadRequest)
            }

            get("/{kit_id}/batches") {
                val batches = call.parameters["kit_id"]?.let { kitId ->
                    kitId.toLongOrNull()?.let {
                        batchService.getBatchesByKitId(kitId.toLong())
                    }
                }
                call.safetyRespond(batches, HttpStatusCode.BadRequest)
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

        }
    }
}