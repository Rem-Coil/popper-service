package com.remcoil.module

import com.remcoil.config.configurationModule
import com.remcoil.model.dto.ActionRequest
import com.remcoil.model.dto.Employee
import com.remcoil.model.dto.EmployeeRole
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals

class ActionModuleTest : BaseModuleTest() {
    private val baseRoute = "/action"

    @Test
    fun `should respond unauthorized`() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        application {
            configurationModule(config)
            actionModule()
        }

        val requestBody = ActionRequest(Clock.System.now().toLocalDateTime(TimeZone.UTC), false, 1, 1)
        val qualityEngineerToken =
            generateToken(Employee(3, "First", "Last", "123", "pass", true, EmployeeRole.QUALITY_ENGINEER))

        val nonAuthPostResponse = client.post(baseRoute) {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }

        val authPostResponse = client.post(baseRoute) {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
            bearerAuth(qualityEngineerToken)
        }

        val nonAuthPutResponse = client.put(baseRoute) {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }

        val authPutResponse = client.put(baseRoute) {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
            bearerAuth(qualityEngineerToken)
        }

        val nonAuthDeleteResponse = client.delete("$baseRoute/{id}") {
            parameter("id", 1)
        }

        val authDeleteResponse = client.delete("$baseRoute/{id}") {
            parameter("id", 1)
            bearerAuth(qualityEngineerToken)
        }

        assertEquals(nonAuthPostResponse.status, HttpStatusCode.Unauthorized)
        assertEquals(authPostResponse.status, HttpStatusCode.Unauthorized)
        assertEquals(nonAuthPutResponse.status, HttpStatusCode.Unauthorized)
        assertEquals(authPutResponse.status, HttpStatusCode.Unauthorized)
        assertEquals(nonAuthDeleteResponse.status, HttpStatusCode.Unauthorized)
        assertEquals(authDeleteResponse.status, HttpStatusCode.Unauthorized)
    }

    @Test
    fun `should respond ok`() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        application {
            configurationModule(config)
            actionModule()
            executeSqlScript("/sql/test_data.sql")
        }

        val requestBody1 = ActionRequest(Clock.System.now().toLocalDateTime(TimeZone.UTC), false, 1, 1)
        val requestBody2 = ActionRequest(Clock.System.now().toLocalDateTime(TimeZone.UTC), false, 2, 1)

        val adminToken =
            generateToken(Employee(1, "FirstName", "LastName", "123", "pass", true, EmployeeRole.ADMIN))
        val operatorToken =
            generateToken(Employee(2, "FirstName", "LastName", "123", "pass", true, EmployeeRole.OPERATOR))

        val postResponse = client.post(baseRoute) {
            contentType(ContentType.Application.Json)
            setBody(requestBody1)
            bearerAuth(adminToken)
        }

        val putResponse = client.post(baseRoute) {
            contentType(ContentType.Application.Json)
            setBody(requestBody2)
            bearerAuth(operatorToken)
        }

        assertEquals(postResponse.status, HttpStatusCode.OK)
        assertEquals(putResponse.status, HttpStatusCode.OK)

    }
}