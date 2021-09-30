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
    // TODO: -1. Телефон не влезеает в int
    // TODO: 0.  Вынести на GitHub код
    // TODO: 1.  Вынести конфигурацию в HOCON файл (порт, данные бд)
    // TODO: 2.  Настроить DI
    // TODO: 3.  Настроить миграции
    // TODO: 4.  Раскидать по папкам

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

