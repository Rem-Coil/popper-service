package com.remcoil.data

import com.remcoil.config.AppConfig
import org.flywaydb.core.Flyway

fun migrate(config: AppConfig) {
    Flyway.configure()
        .dataSource(config.database.url, config.database.user, config.database.password)
        .load()
        .migrate()
}