package com.remcoil.service.v2

import com.remcoil.dao.v2.BatchDao
import com.remcoil.data.model.v2.Batch
import com.remcoil.data.model.v2.Kit
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

    suspend fun createByKit(kit: Kit, startNumber: Int) {
        val batches = mutableListOf<Batch>()
        for (i in startNumber..kit.batchesQuantity) {
            batches.add(Batch(batchNumber = i.toString(), kitId = kit.id))
        }
        val createdBatches = batchDao.batchCreate(batches)
        productService.createByKitAndBatches(kit, createdBatches)
    }

    suspend fun updateBatchSize(oldKit: Kit, kit: Kit) {
        if (oldKit.batchSize == kit.batchSize) {
            return
        }
        val batches = batchDao.getByKitId(kit.id)
        for (batch in batches) {
            if (oldKit.batchSize > kit.batchSize) {
                productService.reduceProductsQuantity(
                    batch.id,
                    excessNumber = oldKit.batchSize - kit.batchSize
                )
            } else {
                productService.increaseProductsQuantity(
                    batch.id,
                    requiredNumber = kit.batchSize - oldKit.batchSize
                )
            }
        }
        logger.info("Обновили размер партий")
    }

    suspend fun updateBatchesQuantity(oldKit: Kit, kit: Kit) {
        if (oldKit.batchesQuantity == kit.batchesQuantity) {
            return
        }
        if (oldKit.batchesQuantity > kit.batchesQuantity) {
            val batches = batchDao.getByKitId(kit.id)
            for (i in 1..oldKit.batchesQuantity - kit.batchesQuantity) {
                batchDao.deleteById(batches[batches.size - i].id)
            }
        } else {
            for (i in 1..kit.batchesQuantity - oldKit.batchesQuantity) {
                createByKit(kit, oldKit.batchesQuantity + i)
            }
        }
        logger.info("Обновили число партий")
    }
}