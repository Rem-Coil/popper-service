package com.remcoil

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.remcoil.config.AppConfig
import com.remcoil.di.*
import com.typesafe.config.ConfigFactory
import io.github.config4k.extract
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import org.flywaydb.core.Flyway
import org.kodein.di.ktor.di

fun Application.configureApplication() {
    val config = ConfigFactory.load().extract<AppConfig>()

    Flyway.configure()
        .dataSource(config.database.url, config.database.user, config.database.password)
        .load()
        .migrate()

    di {
        coreComponents(config)
        operatorComponents()
        taskComponents()
        actionComponents()
        commentComponents()
        bobbinComponents()
        batchComponents()

        v2Components()
    }

    install(CORS) {
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowHeader(HttpHeaders.Authorization)
        exposeHeader(HttpHeaders.AccessControlAllowOrigin)
        anyHost()
        allowNonSimpleContentTypes = true
        allowCredentials = true
    }

    install(ContentNegotiation) {
        json()
    }

    install(Authentication) {
        jwt("jwt-access") {
            verifier(
                JWT
                    .require(Algorithm.HMAC256(config.jwt.secret))
                    .build()
            )

            validate { credential ->
                if (credential.payload.getClaim("role").asString() != "") {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }
}
