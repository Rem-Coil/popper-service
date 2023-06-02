package com.remcoil.module

import com.remcoil.config.configurationModule
import com.remcoil.model.dto.Action
import com.remcoil.model.dto.ControlAction
import com.remcoil.model.dto.Employee
import com.remcoil.model.dto.EmployeeRole
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
            controlActionModule()
            executeSqlScript("/sql/test_data.sql")
        }

        val adminToken =
            generateToken(Employee(1, "FirstName", "LastName", "123", "pass", true, EmployeeRole.ADMIN))

        val batchModuleResponse = client.delete("$baseRoute/{1}/history") {
            parameter("id", 1)
            bearerAuth(adminToken)
        }

        val actionModuleResponse = client.get("/action")
        val controlActionModuleResponse = client.get("/control_action")

        assertEquals(HttpStatusCode.OK, batchModuleResponse.status)
        assertEquals(emptyList<Action>(), actionModuleResponse.body())
        assertEquals(emptyList<ControlAction>(), controlActionModuleResponse.body())
    }
}