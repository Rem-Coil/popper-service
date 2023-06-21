package com.remcoil.module

import com.remcoil.config.configurationModule
import com.remcoil.model.dto.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

class BatchModuleTest : BaseModuleTest() {
    private val baseRoute = "/batch"

    @Test
    fun `should clear action history`() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        application {
            configurationModule(config)
            batchModule()
            actionModule()
            productModule()
            controlActionModule()
            executeSqlScript("/sql/test_data_1.sql")
            executeSqlScript("/sql/test_data_2.sql")
        }

        val adminToken =
            generateToken(Employee(1, "FirstName", "LastName", "123", "pass", true, EmployeeRole.ADMIN))

        val batchModuleResponse = client.delete("$baseRoute/{1}/history") {
            parameter("id", 1)
            bearerAuth(adminToken)
        }

        val actionModuleResponse = client.get("/action")
        val controlActionModuleResponse = client.get("/control_action")
        val productModuleResponse = client.get("/product")

        assertEquals(HttpStatusCode.OK, batchModuleResponse.status)
        assertEquals(emptyList<Action>(), actionModuleResponse.body())
        assertEquals(emptyList<ControlAction>(), controlActionModuleResponse.body())
        assertEquals(2, productModuleResponse.body<List<Product>>().size)
    }
}