package com.remcoil

import com.remcoil.config.AppConfig
import com.remcoil.data.migrate
import com.remcoil.di.*
import com.remcoil.module.action.actionModule
import com.remcoil.module.batch.batchModule
import com.remcoil.module.bobbin.bobbinModule
import com.remcoil.module.operator.operatorModule
import com.remcoil.module.site.siteModule
import com.remcoil.module.task.taskModule
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.typesafe.config.ConfigFactory
import io.github.config4k.extract
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*
import org.kodein.di.ktor.di


fun main() {

    val config = ConfigFactory.load().extract<AppConfig>()

    migrate(config)

    embeddedServer(Netty, port = config.http.port, host = config.http.host) {
        di {
            coreComponents(config)
            operatorComponents()
            taskComponents()
            actionComponents()
            bobbinComponents()
            batchComponents()
        }
        operatorModule()
        taskModule()
        actionModule()
        bobbinModule()
        batchModule()
        configureSerialization()
        siteModule(config)
        install(CORS) {
            allowMethod(HttpMethod.Get)
            allowMethod(HttpMethod.Post)
            allowMethod(HttpMethod.Put)
            allowMethod(HttpMethod.Delete)
            allowMethod(HttpMethod.Patch)
            exposeHeader(HttpHeaders.AccessControlAllowOrigin)
            anyHost()
            allowNonSimpleContentTypes = true
            allowCredentials = true
        }

    }.start(wait = true)
}
