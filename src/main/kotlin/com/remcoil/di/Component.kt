package com.remcoil.di

import com.remcoil.config.AppConfig
import com.remcoil.dao.action.ActionDao
import com.remcoil.dao.action.FullActionDao
import com.remcoil.dao.batch.BatchDao
import com.remcoil.dao.bobbin.BobbinDao
import com.remcoil.dao.comment.CommentDao
import com.remcoil.dao.operator.OperatorDao
import com.remcoil.dao.task.TaskDao
import com.remcoil.service.action.ActionService
import com.remcoil.service.action.FullActionService
import com.remcoil.service.batch.BatchService
import com.remcoil.service.bobbin.BobbinService
import com.remcoil.service.comment.CommentService
import com.remcoil.service.operator.OperatorService
import com.remcoil.service.task.TaskService
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

fun DI.Builder.operatorComponents() {
    bind<OperatorDao>() with singleton { OperatorDao(instance()) }
    bind<OperatorService>() with singleton { OperatorService(instance(), instance<AppConfig>().jwt) }
}

fun DI.Builder.taskComponents() {
    bind<TaskDao>() with singleton { TaskDao(instance()) }
    bind<TaskService>() with singleton { TaskService(instance(), instance(), instance())  }
}

fun DI.Builder.actionComponents() {
    bind<ActionDao>() with singleton { ActionDao(instance()) }
    bind<FullActionDao>() with singleton { FullActionDao(instance()) }
    bind<FullActionService>() with singleton { FullActionService(instance()) }
    bind<ActionService>() with singleton { ActionService(instance(), instance()) }
}

fun DI.Builder.commentComponents() {
    bind<CommentDao>() with singleton { CommentDao(instance()) }
    bind<CommentService>() with singleton { CommentService(instance(), instance()) }
}

fun DI.Builder.bobbinComponents() {
    bind<BobbinDao>() with singleton { BobbinDao(instance()) }
    bind<BobbinService>() with singleton { BobbinService(instance()) }
}

fun DI.Builder.batchComponents() {
    bind<BatchDao>() with singleton { BatchDao(instance()) }
    bind<BatchService>() with singleton { BatchService(instance(), instance(), instance(), instance()) }
}

fun DI.Builder.v2Components() {
    bind<com.remcoil.dao.v2.ActionDao>() with singleton { com.remcoil.dao.v2.ActionDao(instance()) }
    bind<com.remcoil.dao.v2.BatchDao>() with singleton { com.remcoil.dao.v2.BatchDao(instance()) }
    bind<com.remcoil.dao.v2.KitDao>() with singleton { com.remcoil.dao.v2.KitDao(instance()) }
    bind<com.remcoil.dao.v2.SpecificationDao>() with singleton { com.remcoil.dao.v2.SpecificationDao(instance()) }
    bind<com.remcoil.dao.v2.ProductDao>() with singleton { com.remcoil.dao.v2.ProductDao(instance()) }

    bind<com.remcoil.service.v2.ActionService>() with singleton { com.remcoil.service.v2.ActionService(instance(), instance()) }
    bind<com.remcoil.service.v2.BatchService>() with singleton { com.remcoil.service.v2.BatchService(instance(), instance())}
    bind<com.remcoil.service.v2.KitService>() with singleton { com.remcoil.service.v2.KitService(instance(), instance(), instance())}
    bind<com.remcoil.service.v2.SpecificationService>() with singleton { com.remcoil.service.v2.SpecificationService(instance())}
    bind<com.remcoil.service.v2.ProductService>() with singleton { com.remcoil.service.v2.ProductService(instance())}
}