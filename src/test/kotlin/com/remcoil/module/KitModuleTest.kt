package com.remcoil.module

import com.remcoil.config.configurationModule
import com.remcoil.model.dto.Employee
import com.remcoil.model.dto.EmployeeRole
import com.remcoil.model.dto.Kit
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

class KitModuleTest : BaseModuleTest() {
    private val baseRoute = "/kit"

    @Test
    fun `should respond unauthorized`() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        application {
            configurationModule(config)
            kitModule()
        }

        val requestBody = Kit(1, "Test", 1,2, 1)
        val qualityEngineerToken =
            generateToken(Employee(3, "First", "Last", "123", "pass", true, EmployeeRole.QUALITY_ENGINEER))
        val operatorToken =
            generateToken(Employee(3, "First", "Last", "123", "pass", true, EmployeeRole.OPERATOR))

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
            kitModule()
            executeSqlScript("/sql/test_data.sql")
        }

        val postRequestBody = Kit(0, "Test", 1,2, 1)
        val putRequestBody = Kit(2, "Test", 1,2, 1)

        val adminToken =
            generateToken(Employee(1, "FirstName", "LastName", "123", "pass", true, EmployeeRole.ADMIN))

        val postResponse = client.post(baseRoute) {
            contentType(ContentType.Application.Json)
            setBody(postRequestBody)
            bearerAuth(adminToken)
        }

        val putResponse = client.post(baseRoute) {
            contentType(ContentType.Application.Json)
            setBody(putRequestBody)
            bearerAuth(adminToken)
        }

        assertEquals(postResponse.status, HttpStatusCode.OK)
        assertEquals(putResponse.status, HttpStatusCode.OK)
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
            kitModule()
            executeSqlScript("/sql/test_data.sql")
        }

        val postRequestBody = Kit(0, "Test", 1,2, 0)
        val putRequestBody = Kit(2, "Test", 1,2, 0)

        val adminToken =
            generateToken(Employee(1, "FirstName", "LastName", "123", "pass", true, EmployeeRole.ADMIN))

        val postResponse = client.post(baseRoute) {
            contentType(ContentType.Application.Json)
            setBody(postRequestBody)
            bearerAuth(adminToken)
        }

        val putResponse = client.post(baseRoute) {
            contentType(ContentType.Application.Json)
            setBody(putRequestBody)
            bearerAuth(adminToken)
        }

        assertEquals(postResponse.status, HttpStatusCode.BadRequest)
        assertEquals(putResponse.status, HttpStatusCode.BadRequest)
    }
}