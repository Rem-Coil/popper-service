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
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
class BobbinDaoTest {
    companion object {
        lateinit var databaseFactory: DatabaseFactory
        lateinit var database: Database
        lateinit var taskDao: TaskDao
        lateinit var bobbinDao: BobbinDao
        lateinit var task: Task

        @BeforeAll
        @JvmStatic
        fun setup() = runTest {
            databaseFactory = DatabaseFactory()
            database = databaseFactory.connect()
            taskDao = TaskDao(database)
            bobbinDao = BobbinDao(database)
            task = taskDao.createTask(Task(TaskIdentity("NASA", "1959", 5)))
        }

        @AfterAll
        @JvmStatic
        fun close() {
            databaseFactory.close()
        }
    }

    @Test
    fun `get bobbin test`() = runTest {
        val bobbins = bobbinDao.getByTaskId(task.id)
        assertEquals(5, bobbins.size)
        assertEquals(bobbinDao.getById(bobbins[1].id)!!.id, bobbins[1].id)
        taskDao.createTask(Task(TaskIdentity("BA", "1916", 1)))
        assertEquals(6, bobbinDao.getAll().size)
    }
}