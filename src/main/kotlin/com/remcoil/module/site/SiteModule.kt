package com.remcoil.module.site

import com.remcoil.config.AppConfig
import io.ktor.application.*
import io.ktor.http.content.*
import io.ktor.routing.*

fun Application.siteModule(config: AppConfig) {
    routing {
        static("/swagger") {
            resources(resourcePackage = config.swagger.resourcePackage)
            defaultResource(resource = config.swagger.resource, resourcePackage = config.swagger.resourcePackage)
        }

        static {
            resources(resourcePackage = config.web.resourcePackage)
            defaultResource(resource = config.web.resource, resourcePackage = config.web.resourcePackage)
        }
    }
}