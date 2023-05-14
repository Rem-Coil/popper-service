package com.remcoil

import com.remcoil.config.AppConfig
import com.remcoil.module.action.actionModule
import com.remcoil.module.batch.batchModule
import com.remcoil.module.bobbin.bobbinModule
import com.remcoil.module.comment.commentModule
import com.remcoil.module.operator.operatorModule
import com.remcoil.module.site.siteModule
import com.remcoil.module.task.taskModule
import com.remcoil.module.v2.*
import com.typesafe.config.ConfigFactory
import io.github.config4k.extract
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*


fun main() {
    val config = ConfigFactory.load().extract<AppConfig>()

    embeddedServer(Netty, port = config.http.port, host = config.http.host, module = Application::popper).start(wait = true)
}

fun Application.popper() {
    configureApplication()

    operatorModule()
    taskModule()
    actionModule()
    bobbinModule()
    batchModule()
    commentModule()
    siteModule()

    actionModuleV2()
    productModuleV2()
    batchModuleV2()
    kitModuleV2()
    specificationModuleV2()
    controlActionModuleV2()
    operationTypeModuleV2()
    employeeModuleV2()
}
