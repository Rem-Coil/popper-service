package com.remcoil.module.v2

import com.remcoil.data.model.v2.Kit
import com.remcoil.service.v2.BatchService
import com.remcoil.service.v2.KitService
import com.remcoil.utils.exceptions.EntryDoesNotExistException
import com.remcoil.utils.respondNullable
import com.remcoil.utils.safetyReceive
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
        route("/v2/kit") {
            get {
                val kits = kitService.getAllKits()
                call.respond(kits)
            }

            get("/{id}/batches") {
                val batches = call.parameters["id"]?.let { kitId ->
                    kitId.toLongOrNull()?.let {
                        batchService.getBatchesByKitId(it)
                    }
                }
                call.respondNullable(batches, onNull = HttpStatusCode.BadRequest)
            }


            get("/{id}") {
                try {
                    val kit = call.parameters["id"]?.let { id ->
                        id.toLongOrNull()?.let {
                            kitService.getKitById(it)
                        }
                    }
                    call.respondNullable(kit, onNull = HttpStatusCode.BadRequest)
                } catch (e: EntryDoesNotExistException) {
                    call.respond(HttpStatusCode.NotFound, e.message.toString())
                }
            }

            get("/{id}/progress") {
                try {
                    val progress = call.parameters["id"]?.let { id ->
                        id.toLongOrNull()?.let {
                            kitService.getKitProgressById(it)
                        }
                    }
                    call.respondNullable(progress, onNull = HttpStatusCode.BadRequest)
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
                        kitService.deleteKitById(it)
                    }
                }
                call.respondNullable(result, onNull = HttpStatusCode.BadRequest)
            }

        }
    }
}