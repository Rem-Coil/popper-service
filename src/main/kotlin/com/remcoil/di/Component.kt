package com.remcoil.di

import com.remcoil.config.AppConfig
import com.remcoil.dao.operator.OperatorDao
import com.remcoil.service.operator.OperatorService
import org.jetbrains.exposed.sql.Database
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

fun DI.Builder.coreComponents(config: AppConfig) {
    bind<AppConfig>() with singleton { config }
    bind<Database>() with singleton {
        Database.connect(
            config.database.url,
            user = config.database.user,
            password = config.database.password
        )
    }
}

fun DI.Builder.operatorComponents() {
    bind<OperatorDao>() with singleton { OperatorDao(instance()) }
    bind<OperatorService>() with singleton { OperatorService(instance(), instance<AppConfig>().jwt) }
}