package com.test.remcoil.utils.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import java.sql.Connection
import java.sql.DriverManager

class DatabaseFactory {
    lateinit var source: HikariDataSource

    fun close() {
        source.close()
    }

    fun connect(): Database {
        val source = hikari()
        Flyway.configure()
            .dataSource(source)
            .load()
            .migrate()
        return Database.connect(source)
    }

    private fun hikari(): HikariDataSource {
        val config = HikariConfig()
        config.driverClassName = "org.h2.Driver"
        config.jdbcUrl = "jdbc:h2:mem:test_db;MODE=PostgreSQL;"
        config.maximumPoolSize = 2
        config.isAutoCommit = true
        config.validate()
        source = HikariDataSource(config)
        return source
    }
}