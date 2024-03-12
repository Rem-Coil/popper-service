package com.remcoil.config

data class AppConfig(
    val database: DatabaseConfig,
    val http: HttpConfig,
    val jwt: JwtConfig
)