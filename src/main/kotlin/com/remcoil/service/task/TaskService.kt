package com.remcoil.service.task

import com.remcoil.dao.task.TaskDao
import com.remcoil.data.model.bobbin.Bobbin
import com.remcoil.data.model.task.FullTask
import com.remcoil.data.model.task.Task
import com.remcoil.data.model.task.TaskIdentity
import com.remcoil.service.batch.BatchService
import com.remcoil.service.bobbin.BobbinService
import com.remcoil.utils.logger

class TaskService(
    private val taskDao: TaskDao,
    private val batchService: BatchService,
    private val bobbinService: BobbinService
) {

    fun getAll(): List<Task> {
        val tasks = taskDao.getAllTasks()
        logger.info("Вернули все ТЗ")
        return tasks
    }

    fun getById(taskId: Int): Task? {
        val task = taskDao.getById(taskId)
        logger.info("Вернули данные от ТЗ - $taskId")
        return task
    }

    fun getFullById(taskId: Int): FullTask {
        val task = getById(taskId)
        return toFullTask(task!!)
    }

    fun getAllFull(): List<FullTask> {
        val tasks = getAll()
        val fullTasks = mutableListOf<FullTask>()

        for (task in tasks) {
            fullTasks.add(toFullTask(task))
        }
        return fullTasks
    }

    private fun toFullTask(task: Task): FullTask {
        val batches = batchService.getByTaskId(task.id)
        val fullTask = FullTask(task.id, task.taskName, task.taskNumber, 0, 0, 0, 0, 0, 0, 0, 0)

        for (batch in batches) {
            val fullBatch = batchService.getFullById(batch.id)
            fullTask.quantity += fullBatch.quantity
            fullTask.winding += fullBatch.winding
            fullTask.output += fullBatch.output
            fullTask.isolation += fullBatch.isolation
            fullTask.molding += fullBatch.molding
            fullTask.crimping += fullBatch.crimping
            fullTask.quality += fullBatch.quality
            fullTask.testing += fullBatch.testing
        }

        return fullTask
    }

    suspend fun deleteTask(taskId: Int) {
        taskDao.deleteTask(taskId)
        logger.info("Данные о ТЗ удалены")
    }

    suspend fun createTask(taskIdentity: TaskIdentity): Task {
        val task = taskDao.createTask(Task(taskIdentity))
        for (i in 0 until taskIdentity.batchNumber) {
            batchService.createByTask(task, taskIdentity.batchSize, i+1)
        }
        logger.info("Создано ТЗ - ${task.taskName} ${task.taskNumber}")
        return task
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
        logger.info("Обновили ТЗ")
    }

    fun getBobbinsByTaskId(taskId: Int): List<Bobbin> {
        val batches = batchService.getByTaskId(taskId)
        return bobbinService.getByBatches(batches)
    }
}