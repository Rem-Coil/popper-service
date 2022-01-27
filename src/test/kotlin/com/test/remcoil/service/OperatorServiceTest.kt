package com.test.remcoil.service

import com.remcoil.config.JwtConfig
import com.remcoil.dao.operator.OperatorDao
import com.remcoil.data.model.operator.Operator
import com.remcoil.data.model.operator.OperatorCredentials
import com.remcoil.service.operator.OperatorService
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class OperatorServiceTest {

    @Test
    fun `test get operator`() {
        val operatorDao = mockk<OperatorDao>()
        val config = JwtConfig("abc", 1)

        every { operatorDao.getOperator(any()) } returns  Operator(
            1,
            "Michael",
            "Mike",
            "Collins",
            "1624-07-1969",
            "apollo",
            true
        )

        val operatorService = spyk(OperatorService(operatorDao, config), recordPrivateCalls = true)
        every { operatorService["generateToken"](allAny<Operator>()) } returns "123"

        assertEquals("123", operatorService.getOperator(OperatorCredentials("1624-07-1969", "apollo")))
    }

}