package com.test.remcoil.dao

import com.remcoil.dao.bobbin.BobbinDao
import com.remcoil.dao.task.TaskDao
import com.remcoil.data.model.task.FullTask
import com.remcoil.data.model.task.Task
import com.remcoil.data.model.task.TaskIdentity
import com.test.remcoil.utils.database.DatabaseFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.jetbrains.exposed.sql.Database
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*

@ExperimentalCoroutinesApi
class TaskDaoTest {
    companion object {
        lateinit var databaseFactory: DatabaseFactory
        lateinit var database: Database
        lateinit var taskDao: TaskDao
        lateinit var bobbinDao: BobbinDao
        lateinit var taskIdentity: TaskIdentity
        lateinit var task: Task

        @BeforeAll
        @JvmStatic
        fun setup() {
            databaseFactory = DatabaseFactory()
            database = databaseFactory.connect()
            taskDao = TaskDao(database)
            bobbinDao = BobbinDao(database)
            taskIdentity = TaskIdentity("NASA", "1959", 5)
        }

        @AfterAll
        @JvmStatic
        fun close() {
            databaseFactory.close()
        }
    }

    @BeforeEach
    fun insertTask() = runTest {
        task = taskDao.createTask(Task(taskIdentity))
    }

    @AfterEach
    fun removeTask() = runTest {
        taskDao.deleteTask(task.id)
    }

    @Test
    fun `create and get task test`() {
        assertEquals(task, taskDao.getById(task.id))
        assertEquals(task, taskDao.getAllTasks()[0])
    }

    @Test
    fun `delete task test`(): Unit = runTest {
        taskDao.deleteTask(task.id)
        assertTrue(taskDao.getAllTasks().isEmpty())
        val taskId = taskDao.createTask(Task(TaskIdentity("BA", "1916", 1))).id
        assertEquals(1, bobbinDao.getAll().size)
        taskDao.deleteTask(taskId)
    }
}