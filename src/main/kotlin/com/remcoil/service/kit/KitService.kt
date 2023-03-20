package com.remcoil.service.kit

import com.remcoil.dao.kit.KitDao
import com.remcoil.dao.specification.SpecificationDao
import com.remcoil.data.model.kit.Kit
import com.remcoil.data.model.product.Product
import com.remcoil.service.batch.BatchService
import com.remcoil.service.product.ProductService
import com.remcoil.utils.exceptions.EntryDoesNotExistException
import com.remcoil.utils.logger

class KitService(
    private val kitDao: KitDao,
    private val batchService: BatchService,
    private val productService: ProductService,
    private val specificationDao: SpecificationDao
) {

    suspend fun getAllKits(): List<Kit> {
        val kits = kitDao.getAll()
        logger.info("Вернули все наборы")
        return kits
    }

    suspend fun getKitById(id: Long): Kit {
        val kit = kitDao.getById(id)
        logger.info("Вернули данные от наборе - $id")
        return kit ?: throw EntryDoesNotExistException("Набор с id = $id не существует")
    }

    suspend fun getKitsBySpecificationId(id: Long): List<Kit> {
        val kits = kitDao.getBySpecificationId(id)
        logger.info("Вернули наборы с ТЗ - $id")
        return kits
    }

//    suspend fun getFullById(taskId: Int): FullTask {
//        val task = getKitById(taskId)
//        return toFullTask(task)
//    }
//
//    suspend fun getAllFull(): List<FullTask> {
//        val tasks = getAll()
//        val fullTasks = mutableListOf<FullTask>()
//
//        for (task in tasks) {
//            fullTasks.add(toFullTask(task))
//        }
//        return fullTasks
//    }
//
//    private suspend fun toFullTask(task: Specification): FullTask {
//        val batches = batchService.getFullByTaskId(task.id)
//        return FullTask(task.id, task.taskName, task.taskNumber, batches)
//    }

    suspend fun deleteKitById(id: Long) {
        kitDao.deleteById(id)
        logger.info("Данные о ТЗ удалены")
    }

    suspend fun createKit(kit: Kit): Kit {
        val createdKit = kitDao.create(setKitNumber(kit))
        for (i in 1..kit.batchesQuantity) {
            batchService.createByKit(createdKit, i)
        }
        logger.info("Создан набор - ${createdKit.kitNumber}")
        return createdKit
    }

    suspend fun updateKit(kit: Kit) {
        val oldKit = getKitById(kit.id)
        kitDao.update(setKitNumber(kit))
        if (oldKit.kitNumber != kit.kitNumber) {
            updateBatchesNumber(kit)
            logger.info("Обновили номер набора")
        }
        if (oldKit.batchSize != kit.batchSize) {
            updateBatchSize(oldKit, kit)
            logger.info("Обновили размер партий")
        }
        if (oldKit.batchesQuantity != kit.batchesQuantity) {
            updateBatchesQuantity(oldKit, kit)
            logger.info("Обновили число партий")
        }
    }

    private suspend fun updateBatchSize(oldKit: Kit, kit: Kit) {
        val batches = batchService.getBatchesByKitId(kit.id)
        for (batch in batches) {
            val products = productService.getProductsByBatchId(batch.id)
            if (oldKit.batchSize > kit.batchSize) {
                for (i in 1..oldKit.batchSize - kit.batchSize) {
                    productService.deleteProductById(products[products.size - i].id)
                }
            } else {
                for (i in 1..kit.batchSize - oldKit.batchSize) {
                    productService.createProduct(
                        Product(
                            id = 0,
                            productNumber = "${batch.batchNumber} / ${oldKit.batchSize + i}",
                            active = true,
                            batchId = batch.id
                        )
                    )
                }
            }
        }
    }

    private suspend fun updateBatchesQuantity(oldKit: Kit, kit: Kit) {
        if (oldKit.batchesQuantity > kit.batchesQuantity) {
            val batches = batchService.getBatchesByKitId(kit.id)
            for (i in 1..oldKit.batchesQuantity - kit.batchesQuantity) {
                batchService.deleteBatchById(batches[batches.size - i].id)
            }
        } else {
            for (i in 1..kit.batchesQuantity - oldKit.batchesQuantity) {
                batchService.createByKit(kit, oldKit.batchesQuantity + i)
            }
        }
    }

    private suspend fun updateBatchesNumber(kit: Kit) {
        val batches = batchService.getBatchesByKitId(kit.id)
        for (batch in batches) {
            val numberTail = batch.batchNumber.substringAfterLast(" / ")
            batch.batchNumber = "${kit.kitNumber} / $numberTail"
            batchService.updateBatch(batch)
        }
    }

    private suspend fun setKitNumber(kit: Kit): Kit {
        val specification = specificationDao.getById(kit.specificationId)
            ?: throw EntryDoesNotExistException("ТЗ с id - ${kit.specificationId} не существует")
        kit.kitNumber = "${specification.specificationTitle} / ${kit.kitNumber}"
        return kit
    }

    suspend fun getProductsByKitId(id: Long): List<Product> {
        val batches = batchService.getBatchesByKitId(id)
        return productService.getProductsByBatches(batches)
    }
}