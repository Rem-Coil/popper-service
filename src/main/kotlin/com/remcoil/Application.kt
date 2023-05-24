package com.remcoil

import com.remcoil.config.AppConfig
import com.remcoil.config.configurationModule
import com.remcoil.module.*
import com.typesafe.config.ConfigFactory
import io.github.config4k.extract
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*


val config = ConfigFactory.load().extract<AppConfig>()

fun main() {
    embeddedServer(
        Netty,
        port = config.http.port,
        host = config.http.host,
        module = Application::popper
    ).start(wait = true)
}

fun Application.popper() {
    configurationModule(config)

    siteModule()
    actionModule()
    productModule()
    batchModule()
    kitModule()
    specificationModule()
    controlActionModule()
    operationTypeModule()
    employeeModule()
}
