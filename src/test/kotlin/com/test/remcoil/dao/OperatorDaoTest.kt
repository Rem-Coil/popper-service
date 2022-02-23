package com.test.remcoil.dao

import com.remcoil.dao.operator.OperatorDao
import com.remcoil.data.model.operator.Operator
import com.test.remcoil.utils.database.DatabaseFactory
import org.jetbrains.exposed.sql.Database
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*


class OperatorDaoTest {
    companion object {
        private lateinit var databaseFactory: DatabaseFactory
        private lateinit var database: Database
        private lateinit var dao: OperatorDao
        private lateinit var operator: Operator

        @BeforeAll
        @JvmStatic
        internal fun setup() {
            databaseFactory = DatabaseFactory()
            database = databaseFactory.connect()
            dao = OperatorDao(database)
            operator = Operator(
                -1,
                "Michael",
                "Mike",
                "Collins",
                "1624-07-1969",
                "apollo",
                true
            )
        }

        @AfterAll
        @JvmStatic
        internal fun down() {
            databaseFactory.close()
        }
    }

    @BeforeEach
    fun insertOperator() {
        dao.createOperator(operator)
    }

    @AfterEach
    fun removeOperator() {
        dao.trueDeleteOperator(operator.phone)
    }

    @Test
    fun `create and get operator test`() {
        val operatorId = dao.getOperator("1624-07-1969")!!.id
        assertEquals(dao.getById(operatorId), dao.getOperator("1624-07-1969"))
        assertTrue(dao.getAllOperators(true).isNotEmpty())
    }

    @Test
    fun `delete operator test`() {
        val operatorId = dao.getOperator("1624-07-1969")!!.id
        dao.deleteOperator(operatorId)
        assertTrue(dao.getAllOperators(true).isEmpty())
        assertEquals(dao.getOperator("1624-07-1969"), null)
        assertFalse(dao.getById(operatorId)!!.active)
    }
}