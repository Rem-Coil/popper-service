package com.test.remcoil.dao

import com.remcoil.dao.action.ActionDao
import com.remcoil.dao.bobbin.BobbinDao
import com.remcoil.dao.operator.OperatorDao
import com.remcoil.dao.task.TaskDao
import com.remcoil.data.model.action.ActionType
import com.remcoil.data.model.action.Action
import com.remcoil.data.model.operator.Operator
import com.remcoil.data.model.task.Task
import com.remcoil.data.model.task.TaskIdentity
import com.test.remcoil.utils.database.DatabaseFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.*
import org.jetbrains.exposed.sql.Database
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*

@ExperimentalCoroutinesApi
class ActionDaoTest {
    companion object {
        lateinit var databaseFactory: DatabaseFactory
        lateinit var database: Database
        lateinit var taskDao: TaskDao
        lateinit var bobbinDao: BobbinDao
        lateinit var actionDao: ActionDao
        lateinit var operatorDao: OperatorDao
        lateinit var action: Action

        @BeforeAll
        @JvmStatic
        fun setup() = runTest {
            databaseFactory = DatabaseFactory()
            database = databaseFactory.connect()
            taskDao = TaskDao(database)
            bobbinDao = BobbinDao(database)
            actionDao = ActionDao(database)
            operatorDao = OperatorDao(database)
            taskDao.createTask(Task(TaskIdentity("BA", "1916", 1)))
            operatorDao.createOperator(Operator(-1,
                                        "Michael",
                                        "Mike",
                                        "Collins",
                                        "1624-07-1969",
                                        "apollo",
                                        true))
        }

        @AfterAll
        @JvmStatic
        fun close() {
            databaseFactory.close()
        }
    }

    @BeforeEach
    fun insertAction() = runTest {
        action = actionDao.createAction(Action(1, 1, 1,
            ActionType.WINDING.type,
            LocalDateTime(2020, 1,1,12,0,0,0)))
    }

    @AfterEach
    fun removeAction() = runTest {
        actionDao.deleteAction(action.id)
    }

    @Test
    fun `get action test`() {
        assertEquals(actionDao.getByBobbinId(1), actionDao.getByTaskId(1))
        assertEquals(action, actionDao.getAll()[0])
        assertEquals(actionDao.getByBobbinId(1)[0].firstname, "Michael")
        assertEquals(actionDao.getByTaskId(1)[0].surname, "Collins")
    }
}