package com.remcoil.di

import com.remcoil.config.AppConfig
import com.remcoil.dao.*
import com.remcoil.service.*
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

fun DI.Builder.coreComponents(config: AppConfig) {
    bind<AppConfig>() with singleton { config }
    bind<Database>() with singleton {
        Database.connect(hikari(config))
    }
}

private fun hikari(config: AppConfig): HikariDataSource {
    val hikariConfig = HikariConfig()
    hikariConfig.driverClassName = "org.postgresql.Driver"
    hikariConfig.jdbcUrl = config.database.url
    hikariConfig.username = config.database.user
    hikariConfig.password = config.database.password
    hikariConfig.maximumPoolSize = 3
    hikariConfig.isAutoCommit = false
    hikariConfig.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
    hikariConfig.validate()
    return HikariDataSource(hikariConfig)
}

fun DI.Builder.components() {
    bind<ActionDao>() with singleton { ActionDao(instance()) }
    bind<BatchDao>() with singleton { BatchDao(instance()) }
    bind<KitDao>() with singleton { KitDao(instance()) }
    bind<SpecificationDao>() with singleton { SpecificationDao(instance()) }
    bind<ProductDao>() with singleton { ProductDao(instance()) }
    bind<ControlActionDao>() with singleton { ControlActionDao(instance()) }
    bind<OperationTypeDao>() with singleton { OperationTypeDao(instance()) }
    bind<EmployeeDao>() with singleton { EmployeeDao(instance()) }
    bind<AcceptanceActionDao>() with singleton { AcceptanceActionDao(instance()) }

    bind<AcceptanceActionService>() with singleton { AcceptanceActionService(instance()) }
    bind<ActionService>() with singleton { ActionService(instance(), instance(), instance()) }
    bind<BatchService>() with singleton { BatchService(instance(), instance(), instance(), instance(), instance()) }
    bind<KitService>() with singleton { KitService(instance(), instance(), instance(), instance(), instance(), instance(), instance()) }
    bind<SpecificationService>() with singleton { SpecificationService(instance(), instance()) }
    bind<ProductService>() with singleton { ProductService(instance()) }
    bind<ControlActionService>() with singleton { ControlActionService(instance(), instance()) }
    bind<OperationTypeService>() with singleton { OperationTypeService(instance()) }
    bind<EmployeeService>() with singleton { EmployeeService(instance(), instance<AppConfig>().jwt) }
}