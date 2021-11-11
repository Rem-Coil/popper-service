package com.remcoil.config

data class AppConfig(
    val database: DatabaseConfig,
    val http: HttpConfig,
    val swagger: SiteConfig,
    val web: SiteConfig,
    val jwt: JwtConfig
)