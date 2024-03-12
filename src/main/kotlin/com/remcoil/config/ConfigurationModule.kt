package com.remcoil.config

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.remcoil.di.components
import com.remcoil.di.coreComponents
import com.remcoil.model.dto.EmployeeRole
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import org.flywaydb.core.Flyway
import org.kodein.di.ktor.di

fun Application.configurationModule(config: AppConfig) {
    Flyway.configure()
        .dataSource(config.database.url, config.database.user, config.database.password)
        .load()
        .migrate()

    di {
        coreComponents(config)
        components()
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
        val jwtVerifier = JWT
            .require(Algorithm.HMAC256(config.jwt.secret))
            .build()

        jwt("employee-access") {
            verifier(jwtVerifier)
            validate { credential ->
                if (EmployeeRole.OPERATOR.isMatch(credential.payload.getClaim("role").asString()) ||
                    EmployeeRole.ADMIN.isMatch(credential.payload.getClaim("role").asString())
                ) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }

        jwt("quality-engineer-access") {
            verifier(jwtVerifier)
            validate { credential ->
                if (EmployeeRole.QUALITY_ENGINEER.isMatch(credential.payload.getClaim("role").asString()) ||
                    EmployeeRole.ADMIN.isMatch(credential.payload.getClaim("role").asString())
                ) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }


        jwt("admin-access") {
            verifier(jwtVerifier)
            validate { credential ->
                if (EmployeeRole.ADMIN.isMatch(credential.payload.getClaim("role").asString())) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }
}
