package com.remcoil.module

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.remcoil.config.AppConfig
import com.remcoil.config.DatabaseConfig
import com.remcoil.config.HttpConfig
import com.remcoil.config.JwtConfig
import com.remcoil.model.dto.Employee
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName
import java.sql.DriverManager
import java.util.*
import kotlin.test.BeforeTest

open class BaseModuleTest {
    lateinit var config: AppConfig

    private val postgres = PostgreSQLContainer(DockerImageName.parse("postgres:11")).apply {
        this
            .withDatabaseName("popper")
            .withUsername("root")
            .withPassword("root")
    }

    @BeforeTest
    fun setup() {
        postgres.start()

        config = AppConfig(
            DatabaseConfig(postgres.jdbcUrl, "root", "root"),
            HttpConfig(0, ""),
            JwtConfig("secret", 172000000)
        )
    }

    fun executeSqlScript(fileName: String) {
        val script = this::class.java.getResource(fileName)?.readText() ?: ""
        val statements = script.split(";")
        val connection = DriverManager.getConnection(config.database.url, config.database.user, config.database.password)

        statements.forEach { statement ->
            if (statement.isNotBlank()) {
                connection.createStatement().execute(statement)
            }
        }

        connection.close()
    }

    fun generateToken(employee: Employee) = JWT.create()
        .withClaim("id", employee.id)
        .withClaim("first_name", employee.firstName)
        .withClaim("last_name", employee.lastName)
        .withClaim("phone", employee.phone)
        .withClaim("role", employee.role.name)
        .withExpiresAt(Date(System.currentTimeMillis() + config.jwt.time))
        .sign(Algorithm.HMAC256(config.jwt.secret)) ?: ""
}