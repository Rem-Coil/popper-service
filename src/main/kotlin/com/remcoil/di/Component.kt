package com.remcoil.di

import com.remcoil.config.AppConfig
import com.remcoil.dao.action.ActionDao
import com.remcoil.dao.action.FullActionDao
import com.remcoil.dao.batch.BatchDao
import com.remcoil.dao.comment.CommentDao
import com.remcoil.dao.kit.KitDao
import com.remcoil.dao.operator.OperatorDao
import com.remcoil.dao.product.ProductDao
import com.remcoil.dao.specification.SpecificationActionDao
import com.remcoil.dao.specification.SpecificationDao
import com.remcoil.service.action.ActionService
import com.remcoil.service.action.FullActionService
import com.remcoil.service.batch.BatchService
import com.remcoil.service.comment.CommentService
import com.remcoil.service.kit.KitService
import com.remcoil.service.operator.OperatorService
import com.remcoil.service.product.ProductService
import com.remcoil.service.specification.SpecificationActionService
import com.remcoil.service.specification.SpecificationService
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

fun DI.Builder.specificationComponents() {
    bind<SpecificationDao>() with singleton { SpecificationDao(instance()) }
    bind<SpecificationService>() with singleton { SpecificationService(instance(), instance()) }
}

fun DI.Builder.kitComponents() {
    bind<KitDao>() with singleton { KitDao(instance()) }
    bind<KitService>() with singleton { KitService(instance(), instance(), instance(), instance()) }
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

fun DI.Builder.productComponents() {
    bind<ProductDao>() with singleton { ProductDao(instance()) }
    bind<ProductService>() with singleton { ProductService(instance()) }
}

fun DI.Builder.batchComponents() {
    bind<BatchDao>() with singleton { BatchDao(instance()) }
    bind<BatchService>() with singleton { BatchService(instance(), instance()) }
}

fun DI.Builder.specificationActionComponents() {
    bind<SpecificationActionDao>() with singleton { SpecificationActionDao(instance()) }
    bind<SpecificationActionService>() with singleton { SpecificationActionService(instance()) }
}