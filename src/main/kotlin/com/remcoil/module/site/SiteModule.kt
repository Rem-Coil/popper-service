package com.remcoil.module.site

import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*

fun Application.siteModule() {
    routing {
        static("/swagger") {
            static("/v1") {
                resources(resourcePackage = "swagger.v1")
                defaultResource(resource = "index.html", resourcePackage = "swagger.v1")
            }

            static("/v2") {
                resources(resourcePackage = "swagger.v2")
                defaultResource(resource = "index.html", resourcePackage = "swagger.v2")
            }

            resources(resourcePackage = "swagger")
        }

        static {
            resources(resourcePackage = "web")
            defaultResource(resource = "index.html", resourcePackage = "web")
        }
    }
}