package com.remcoil.config

data class DatabaseConfig(
    val url: String,
    val driver: String,
    val user: String,
    val password: String
)