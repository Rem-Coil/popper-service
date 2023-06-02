package com.remcoil.module

import com.remcoil.config.configurationModule
import com.remcoil.model.dto.Employee
import com.remcoil.model.dto.EmployeeRole
import com.remcoil.model.dto.OperationType
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

class OperationTypeModuleTest : BaseModuleTest() {
    private val baseRoute = "/operation_type"

    @Test
    fun `should respond unauthorized`() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        application {
            configurationModule(config)
            operationTypeModule()
        }

        val requestBody = OperationType(0, "test", 1, 1)
        val operatorToken =
            generateToken(Employee(3, "First", "Last", "123", "pass", true, EmployeeRole.OPERATOR))
        val qualityEngineerToken =
            generateToken(Employee(3, "First", "Last", "123", "pass", true, EmployeeRole.QUALITY_ENGINEER))


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
            bearerAuth(qualityEngineerToken)
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
            operationTypeModule()
            executeSqlScript("/sql/test_data.sql")
        }

        val requestBody1 = OperationType(0, "test", 3, 1)
        val requestBody2 = OperationType(0, "test_1", 3, 1)

        val adminToken =
            generateToken(Employee(1, "FirstName", "LastName", "123", "pass", true, EmployeeRole.ADMIN))

        val postResponse = client.post(baseRoute) {
            contentType(ContentType.Application.Json)
            setBody(requestBody1)
            bearerAuth(adminToken)
        }

        val putResponse = client.put(baseRoute) {
            contentType(ContentType.Application.Json)
            setBody(requestBody2)
            bearerAuth(adminToken)
        }

        assertEquals(HttpStatusCode.OK, postResponse.status)
        assertEquals(HttpStatusCode.OK, putResponse.status)
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
            operationTypeModule()
            executeSqlScript("/sql/test_data.sql")
        }

        val requestBody1 = OperationType(0, "test", 1, 1)
        val requestBody2 = OperationType(2, "test_1", 1, 1)

        val adminToken =
            generateToken(Employee(1, "FirstName", "LastName", "123", "pass", true, EmployeeRole.ADMIN))

        val postResponse = client.post(baseRoute) {
            contentType(ContentType.Application.Json)
            setBody(requestBody1)
            bearerAuth(adminToken)
        }

        val putResponse = client.post(baseRoute) {
            contentType(ContentType.Application.Json)
            setBody(requestBody2)
            bearerAuth(adminToken)
        }

        assertEquals(HttpStatusCode.Conflict, postResponse.status)
        assertEquals(HttpStatusCode.Conflict, putResponse.status)
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
            operationTypeModule()
            executeSqlScript("/sql/test_data.sql")
        }

        val requestBody = OperationType(0, "test", 1, 0)

        val adminToken =
            generateToken(Employee(0, "FirstName", "LastName", "123", "pass", true, EmployeeRole.ADMIN))

        val postResponse = client.post(baseRoute) {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
            bearerAuth(adminToken)
        }

        assertEquals(HttpStatusCode.BadRequest, postResponse.status)
    }
}