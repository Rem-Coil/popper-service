package com.remcoil.service.batch

import com.remcoil.dao.batch.BatchDao
import com.remcoil.data.model.action.ActionType
import com.remcoil.data.model.batch.Batch
import com.remcoil.data.model.batch.FullBatch
import com.remcoil.data.model.bobbin.Bobbin
import com.remcoil.data.model.task.Task
import com.remcoil.service.action.ActionService
import com.remcoil.service.bobbin.BobbinService
import com.remcoil.utils.logger

class BatchService(
    private val batchDao: BatchDao,
    private val bobbinService: BobbinService,
    private val actionService: ActionService
) {
    fun getAll(): List<Batch> {
        val batches = batchDao.getAll()
        logger.info("Отдали все партии")
        return batches
    }

    fun getById(id: Long): Batch? {
        val batch = batchDao.getById(id)
        logger.info("Отдали партию с id = $id")
        return batch
    }

    fun getByTaskId(taskId: Int): List<Batch> {
        val batches = batchDao.getByTaskId(taskId)
        logger.info("Отдали партии для ТЗ - $taskId")
        return batches
    }

    fun getFullById(batchId: Long): FullBatch {
        val batch = getById(batchId)
        return toFullBatch(batch!!)
    }

    fun getAllFull(): List<FullBatch> {
        val batches = getAll()
        val fullBatches = mutableListOf<FullBatch>()

        for (batch in batches) {
            fullBatches.add(toFullBatch(batch))
        }
        return fullBatches
    }

    private fun toFullBatch(batch: Batch): FullBatch {
        val quantity = bobbinService.getByBatchId(batch.id).count()
        val fullBatch = FullBatch(id = batch.id, batchNumber = batch.batchNumber, task_id = batch.taskId, quantity = quantity, winding = 0, output = 0, isolation = 0, molding = 0, crimping = 0, quality = 0, testing = 0,)

        val bobbins = actionService.getFullByBatchId(batch.id).groupBy { it.bobbinId }

        for (bobbin in bobbins) {
            val lastAction = bobbin.value.maxByOrNull { it.doneTime }
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

        return fullBatch
    }

    suspend fun createBatch(batch: Batch): Batch {
        val createdBatch = batchDao.createBatch(batch)
        logger.info("Создали партию с id ${createdBatch.id}")
        return createdBatch
    }


    suspend fun createByTask(task: Task, quantity: Int, batchNumber: Int) {
        val batch = createBatch(Batch(0, task.id, "${task.taskNumber} / $batchNumber"))
        for (i in 0 until quantity) {
            bobbinService.createBobbin(Bobbin(0, batch.id, "${task.taskName} - ${batch.batchNumber} - ${i+1}"))
        }
    }

    suspend fun deleteBatchById(id: Long) {
        batchDao.deleteBatchById(id)
    }
}