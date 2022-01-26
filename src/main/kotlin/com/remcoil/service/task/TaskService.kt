package com.remcoil.service.task

import com.remcoil.dao.task.TaskDao
import com.remcoil.data.model.task.Task
import com.remcoil.data.model.task.TaskIdentity
import com.remcoil.utils.logger

class TaskService(private val taskDao: TaskDao) {

    fun getAllTasks(): List<Task> {
        val tasks = taskDao.getAllTasks()
        logger.info("Вернули все ТЗ")
        return tasks
    }

    fun getById(taskId: Int): Task? {
        val task = taskDao.getById(taskId)
        logger.info("Вернули данные от ТЗ - $taskId")
        return task
    }

    suspend fun deleteTask(taskId: Int) {
        taskDao.deleteTask(taskId)
        logger.info("Данные о ТЗ удалены")
    }

    suspend fun createTask(taskIdentity: TaskIdentity): Task {
        val task = taskDao.createTask(Task(taskIdentity))
        logger.info("Создано ТЗ - ${task.taskName} ${task.taskNumber}")
        return task
    }
}