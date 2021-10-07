package com.remcoil

import com.remcoil.config.AppConfig
import com.remcoil.data.migrate
import com.remcoil.di.coreComponents
import com.remcoil.di.operatorComponents
import com.remcoil.module.site.siteModule
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.remcoil.plugins.*
import com.typesafe.config.ConfigFactory
import io.github.config4k.extract
import org.kodein.di.ktor.di


fun main() {

    val config = ConfigFactory.load().extract<AppConfig>()

    migrate(config)

    embeddedServer(Netty, port = config.http.port, host = config.http.host) {
        di {
            coreComponents(config)
            operatorComponents()
        }
        operatorModule()
        configureSerialization()
        siteModule(config)
    }.start(wait = true)
}

