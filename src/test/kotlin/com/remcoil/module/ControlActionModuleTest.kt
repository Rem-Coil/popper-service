package com.remcoil.module

import com.remcoil.config.configurationModule
import com.remcoil.model.dto.ControlActionRequest
import com.remcoil.model.dto.ControlType
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

class ControlActionModuleTest : BaseModuleTest() {
    private val baseRoute = "/control_action"

    @Test
    fun `should respond unauthorized`() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        application {
            configurationModule(config)
            controlActionModule()
        }

        val requestBody =
            ControlActionRequest(Clock.System.now().toLocalDateTime(TimeZone.UTC), true, false, ControlType.OTK, "Good", 1, 1)
        val operatorToken =
            generateToken(Employee(3, "First", "Last", "123", "pass", true, EmployeeRole.OPERATOR))

        val nonAuthPostResponse = client.post(baseRoute) {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }

        val authPostResponse = client.post(baseRoute) {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
            bearerAuth(operatorToken)
        }

        val nonAuthPutResponse = client.put(baseRoute) {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }

        val authPutResponse = client.put(baseRoute) {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
            bearerAuth(operatorToken)
        }

        val nonAuthDeleteResponse = client.delete("$baseRoute/{id}") {
            parameter("id", 1)
        }

        val authDeleteResponse = client.delete("$baseRoute/{id}") {
            parameter("id", 1)
            bearerAuth(operatorToken)
        }

        assertEquals(HttpStatusCode.Unauthorized, nonAuthPostResponse.status)
        assertEquals(HttpStatusCode.Unauthorized, authPostResponse.status)
        assertEquals(HttpStatusCode.Unauthorized, nonAuthPutResponse.status)
        assertEquals(HttpStatusCode.Unauthorized, authPutResponse.status)
        assertEquals(HttpStatusCode.Unauthorized, nonAuthDeleteResponse.status)
        assertEquals(HttpStatusCode.Unauthorized, authDeleteResponse.status)
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
            controlActionModule()
            executeSqlScript("/sql/test_data_1.sql")
        }

        val requestBody1 =
            ControlActionRequest(Clock.System.now().toLocalDateTime(TimeZone.UTC), true, false, ControlType.OTK, "Good", 1, 1)
        val requestBody2 =
            ControlActionRequest(Clock.System.now().toLocalDateTime(TimeZone.UTC), true, false, ControlType.OTK, "Good", 1, 2)

        val adminToken =
            generateToken(Employee(1, "FirstName", "LastName", "123", "pass", true, EmployeeRole.ADMIN))
        val qualityEngineerToken =
            generateToken(Employee(2, "FirstName", "LastName", "123", "pass", true, EmployeeRole.QUALITY_ENGINEER))

        val postResponse1 = client.post(baseRoute) {
            contentType(ContentType.Application.Json)
            setBody(requestBody1)
            bearerAuth(adminToken)
        }

        val postResponse2 = client.post(baseRoute) {
            contentType(ContentType.Application.Json)
            setBody(requestBody2)
            bearerAuth(qualityEngineerToken)
        }

        assertEquals(postResponse1.status, HttpStatusCode.OK)
        assertEquals(postResponse2.status, HttpStatusCode.OK)

    }

    @Test
    fun `should respond conflict`() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        application {
            configurationModule(config)
            controlActionModule()
            executeSqlScript("/sql/test_data_1.sql")
        }

        val requestBody1 =
            ControlActionRequest(Clock.System.now().toLocalDateTime(TimeZone.UTC), true, false, ControlType.OTK, "Good", 1, 1)
        val requestBody2 =
            ControlActionRequest(Clock.System.now().toLocalDateTime(TimeZone.UTC), true, false, ControlType.OTK, "Good", 1, 1)

        val qualityEngineerToken =
            generateToken(Employee(2, "FirstName", "LastName", "123", "pass", true, EmployeeRole.QUALITY_ENGINEER))

        val postResponse1 = client.post(baseRoute) {
            contentType(ContentType.Application.Json)
            setBody(requestBody1)
            bearerAuth(qualityEngineerToken)
        }

        val postResponse2 = client.post(baseRoute) {
            contentType(ContentType.Application.Json)
            setBody(requestBody2)
            bearerAuth(qualityEngineerToken)
        }

        assertEquals(HttpStatusCode.OK, postResponse1.status)
        assertEquals(HttpStatusCode.Conflict, postResponse2.status)
    }

    @Test
    fun `should respond bad request`() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        application {
            configurationModule(config)
            controlActionModule()
            executeSqlScript("/sql/test_data_1.sql")
        }

        val requestBody1 =
            ControlActionRequest(Clock.System.now().toLocalDateTime(TimeZone.UTC), true, false, ControlType.OTK, "Good", 0, 1)
         val requestBody3 =
            ControlActionRequest(Clock.System.now().toLocalDateTime(TimeZone.UTC), true, false, ControlType.OTK, "Good", 1, 2)

        val adminToken =
            generateToken(Employee(0, "FirstName", "LastName", "123", "pass", true, EmployeeRole.ADMIN))
        val qualityEngineerToken =
            generateToken(Employee(2, "FirstName", "LastName", "123", "pass", true, EmployeeRole.QUALITY_ENGINEER))

        val postResponse1 = client.post(baseRoute) {
            contentType(ContentType.Application.Json)
            setBody(requestBody1)
            bearerAuth(qualityEngineerToken)
        }

        val postResponse3 = client.post(baseRoute) {
            contentType(ContentType.Application.Json)
            setBody(requestBody3)
            bearerAuth(adminToken)
        }

        assertEquals(HttpStatusCode.BadRequest, postResponse1.status)
        assertEquals(HttpStatusCode.BadRequest, postResponse3.status)
    }

    @Test
    fun `should respond not found`() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        application {
            configurationModule(config)
            controlActionModule()
            executeSqlScript("/sql/test_data_1.sql")
        }

          val requestBody2 =
            ControlActionRequest(Clock.System.now().toLocalDateTime(TimeZone.UTC), true, false, ControlType.OTK, "Good", 1, 0)

         val qualityEngineerToken =
            generateToken(Employee(2, "FirstName", "LastName", "123", "pass", true, EmployeeRole.QUALITY_ENGINEER))

        val postResponse2 = client.post(baseRoute) {
            contentType(ContentType.Application.Json)
            setBody(requestBody2)
            bearerAuth(qualityEngineerToken)
        }

        assertEquals(HttpStatusCode.NotFound, postResponse2.status)
    }
}