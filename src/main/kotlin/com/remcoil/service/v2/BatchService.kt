package com.remcoil.service.v2

import com.remcoil.dao.v2.BatchDao
import com.remcoil.data.model.v2.Batch
import com.remcoil.data.model.v2.Kit
import com.remcoil.data.model.v2.Product
import com.remcoil.utils.exceptions.EntryDoesNotExistException
import com.remcoil.utils.logger

class BatchService(
    private val batchDao: BatchDao,
    private val productService: ProductService,
) {
    suspend fun getAllBatches(): List<Batch> {
        val batches = batchDao.getAll()
        logger.info("Отдали все партии")
        return batches
    }

    suspend fun getBatchById(id: Long): Batch {
        val batch = batchDao.getById(id)
        logger.info("Отдали партию с id = $id")
        return batch ?: throw EntryDoesNotExistException("Партии с id - $id не существует")
    }

    suspend fun getBatchesByKitId(id: Long): List<Batch> {
        val batches = batchDao.getByKitId(id)
        logger.info("Отдали партии для набора - $id")
        return batches
    }

    private suspend fun createBatch(batch: Batch): Batch {
        val createdBatch = batchDao.create(batch)
        logger.info("Создали партию с id ${createdBatch.id}")
        return createdBatch
    }

    suspend fun createByKit(kit: Kit, batchNumber: Int): Batch {
        val batch = createBatch(Batch(0, batchNumber.toString(), kit.id))
        createProducts(kit, batch)
        return batch
    }

    private suspend fun createProducts(kit: Kit, batch: Batch) {
        for (i in 1..kit.batchSize) {
            productService.createProduct(
                Product(
                    id = 0,
                    productNumber = "$i",
                    active = true,
                    batchId = batch.id
                )
            )
        }
    }

    suspend fun deleteBatchById(id: Long) {
        batchDao.deleteById(id)
    }
}