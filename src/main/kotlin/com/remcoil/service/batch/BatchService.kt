package com.remcoil.service.batch

import com.remcoil.dao.batch.BatchDao
import com.remcoil.data.model.batch.Batch
import com.remcoil.data.model.bobbin.Bobbin
import com.remcoil.data.model.task.Task
import com.remcoil.service.bobbin.BobbinService
import com.remcoil.utils.logger

class BatchService(private val batchDao: BatchDao, private val bobbinService: BobbinService) {
    fun getById(id: Long): Batch? {
        val batch = batchDao.getById(id)
        logger.info("Отдали партию с id = $id")
        return batch
    }

    fun getAll(): List<Batch> {
        val batches = batchDao.getAll()
        logger.info("Отдали все партии")
        return batches
    }

    fun getByTaskId(id: Int): List<Batch> {
        val batches = batchDao.getByTaskId(id)
        logger.info("Отдали партии для ТЗ - $id")
        return batches
    }

    suspend fun createBatch(batch: Batch): Batch {
        val createdBatch = batchDao.createBatch(batch)
        logger.info("Создали партию с id ${createdBatch.id}")
        return createdBatch
    }

    suspend fun createBatchByTaskAndSize(task: Task, quantity: Int) {
        val batch = createBatch(Batch(0, task.id))
        for (i in 0 until quantity) {
            bobbinService.createBobbin(Bobbin(0, batch.id, "${task.taskName}-${batch.id}-$i"))
        }
    }

    suspend fun deleteBatchById(id: Long) {
        batchDao.deleteBatchById(id)
    }
}