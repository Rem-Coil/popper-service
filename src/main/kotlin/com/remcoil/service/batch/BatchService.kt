package com.remcoil.service.batch

import com.remcoil.dao.batch.BatchDao
import com.remcoil.data.model.batch.Batch
import com.remcoil.data.model.kit.Kit
import com.remcoil.data.model.product.Product
import com.remcoil.service.product.ProductService
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
        val batch = createBatch(Batch(0, "${kit.kitNumber} / $batchNumber", kit.id))
        createBobbins(kit, batch)
        return batch
    }

    private suspend fun createBobbins(kit: Kit, batch: Batch) {
        for (i in 1..kit.batchSize) {
            productService.createProduct(
                Product(
                    id = 0,
                    productNumber = "${batch.batchNumber} / $i",
                    active = true,
                    batchId = batch.id
                )
            )
        }
    }

    suspend fun updateBatch(batch: Batch) {
        batchDao.update(batch)
        logger.info("Обновили партию с id = ${batch.id}")
        updateBobbinNumber(batch)
    }

    private suspend fun updateBobbinNumber(batch: Batch) {
        val bobbins = productService.getProductsByBatchId(batch.id)
        for (bobbin in bobbins) {
            val numberTail = bobbin.productNumber.substringAfterLast(" / ")
            bobbin.productNumber = "${batch.batchNumber} / $numberTail"
            productService.updateProduct(bobbin)
        }
    }

    suspend fun deleteBatchById(id: Long) {
        batchDao.deleteById(id)
    }
}