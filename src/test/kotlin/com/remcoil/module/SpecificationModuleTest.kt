package com.remcoil.module

import com.remcoil.config.configurationModule
import com.remcoil.model.dto.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

class SpecificationModuleTest : BaseModuleTest() {
    private val baseRoute = "/specification"

    @Test
    fun `should respond unauthorized`() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        application {
            configurationModule(config)
            specificationModule()
        }

        val requestBody = Specification(1, "Test", "Bobbin", 10)
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
            specificationModule()
        }

        val operationType = listOf(OperationTypeRequest("first", 1), OperationTypeRequest("first", 2))
        val postRequestBody = SpecificationPostRequest(0, "Test", "Bobbin", 10, operationType)
        val putRequestBody = SpecificationPostRequest(1, "Test", "Bobbin", 10, operationType)


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
}