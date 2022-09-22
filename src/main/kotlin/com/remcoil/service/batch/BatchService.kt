package com.remcoil.service.batch

import com.remcoil.dao.batch.BatchDao
import com.remcoil.dao.task.TaskDao
import com.remcoil.data.model.action.ActionType
import com.remcoil.data.model.batch.Batch
import com.remcoil.data.model.batch.BatchIdentity
import com.remcoil.data.model.batch.FullBatch
import com.remcoil.data.model.bobbin.Bobbin
import com.remcoil.data.model.task.Task
import com.remcoil.service.action.ActionService
import com.remcoil.service.bobbin.BobbinService
import com.remcoil.utils.logger

class BatchService(
    private val batchDao: BatchDao,
    private val taskDao: TaskDao,
    private val bobbinService: BobbinService,
    private val actionService: ActionService
) {
    suspend fun getAll(): List<Batch> {
        val batches = batchDao.getAll()
        logger.info("Отдали все партии")
        return batches
    }

    suspend fun getById(id: Long): Batch? {
        val batch = batchDao.getById(id)
        logger.info("Отдали партию с id = $id")
        return batch
    }

    suspend fun getByTaskId(taskId: Int): List<Batch> {
        val batches = batchDao.getByTaskId(taskId)
        logger.info("Отдали партии для ТЗ - $taskId")
        return batches
    }

    suspend fun getFullById(batchId: Long): FullBatch {
        val batch = getById(batchId)
        return toFullBatch(batch!!)
    }

    suspend fun getFullByTaskId(taskId: Int): List<FullBatch> {
        val batches = getByTaskId(taskId)

        val fullBatches = mutableListOf<FullBatch>()
        for (batch in batches) {
            fullBatches.add(toFullBatch(batch))
        }
        return fullBatches
    }

    suspend fun getAllFull(): List<FullBatch> {
        val batches = getAll()

        val fullBatches = mutableListOf<FullBatch>()
        for (batch in batches) {
            fullBatches.add(toFullBatch(batch))
        }
        return fullBatches
    }

    private suspend fun toFullBatch(batch: Batch): FullBatch {
        val quantity = bobbinService.getByBatchId(batch.id).count { bobbin -> bobbin.active }
        val fullBatch =
            FullBatch(id = batch.id, batchNumber = batch.batchNumber, task_id = batch.taskId, quantity = quantity)

        val bobbins = actionService.getFullByBatchId(batch.id).groupBy { it.bobbinId }

        for (bobbin in bobbins) {
            val actions = bobbin.value.groupBy { it.actionType }
            for (action in actions) {
                val lastAction = action.value.maxByOrNull { it.doneTime }
                val inc = if (lastAction!!.successful) 1 else 0
                when (lastAction.actionType) {
                    ActionType.WINDING.type -> fullBatch.winding += inc
                    ActionType.OUTPUT.type -> fullBatch.output += inc
                    ActionType.ISOLATION.type -> fullBatch.isolation += inc
                    ActionType.MOLDING.type -> fullBatch.molding += inc
                    ActionType.CRIMPING.type -> fullBatch.crimping += inc
                    ActionType.QUALITY.type -> fullBatch.quality += inc
                    ActionType.TESTING.type -> fullBatch.testing += inc
                }
            }
        }
        return fullBatch
    }

    suspend fun createByIdentity(batchIdentity: BatchIdentity): Batch? {
        val task = taskDao.getById(batchIdentity.taskId) ?: return null
        val batches = getByTaskId(batchIdentity.taskId)

        val batchNumber = defineBatchNumber(batches) ?: return null
        val quantity = bobbinService.getByBatchId(batches.first().id).count {bobbin -> bobbin.active }
        val createdBatch = createByTask(task, quantity, batchNumber + 1)
        logger.info("Создали партию с id ${createdBatch.id}")
        return createdBatch
    }

    private fun defineBatchNumber(batches: List<Batch>): Int? {
        return batches.maxOfOrNull { batch -> batch.batchNumber.substringAfterLast(" / ").toInt() }
    }

    private suspend fun createBatch(batch: Batch): Batch {
        val createdBatch = batchDao.createBatch(batch)
        logger.info("Создали партию с id ${createdBatch.id}")
        return createdBatch
    }

    suspend fun createByTask(task: Task, quantity: Int, batchNumber: Int): Batch {
        val batch = createBatch(Batch(0, task.id, "${task.taskNumber} / $batchNumber"))
        createBobbins(task, batch, quantity)
        return batch
    }

    private suspend fun createBobbins(task: Task, batch: Batch, quantity: Int) {
        for (i in 0 until quantity) {
            bobbinService.createBobbin(Bobbin(0, batch.id, "${task.taskName} - ${batch.batchNumber} - ${i + 1}", true))
        }
    }

    suspend fun updateBatch(batch: Batch, task: Task) {
        batchDao.updateBatch(batch)
        logger.info("Обновили партию с id = ${batch.id}")
        updateBobbins(batch, task)
    }

    private suspend fun updateBobbins(batch: Batch, task: Task) {
        val bobbins = bobbinService.getByBatchId(batch.id)
        for (bobbin in bobbins) {
            val numberTail = bobbin.bobbinNumber.substringAfterLast(" - ")
            bobbin.bobbinNumber = "${task.taskName} - ${batch.batchNumber} - $numberTail"
            bobbinService.updateBobbin(bobbin)
        }
    }

    suspend fun deleteBatchById(id: Long) {
        batchDao.deleteBatchById(id)
    }
}