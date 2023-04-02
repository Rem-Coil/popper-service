package com.remcoil.service.v2

import com.remcoil.dao.v2.KitDao
import com.remcoil.data.model.v2.Kit
import com.remcoil.data.model.v2.Product
import com.remcoil.utils.exceptions.EntryDoesNotExistException
import com.remcoil.utils.logger


class KitService(
    private val kitDao: KitDao,
    private val batchService: BatchService,
    private val productService: ProductService,
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

    suspend fun deleteKitById(id: Long) {
        kitDao.deleteById(id)
        logger.info("Данные о ТЗ удалены")
    }

    suspend fun createKit(kit: Kit): Kit {
        val createdKit = kitDao.create(kit)
        for (i in 1..kit.batchesQuantity) {
            batchService.createByKit(createdKit, i)
        }
        logger.info("Создан набор - ${createdKit.kitNumber}")
        return createdKit
    }

    suspend fun updateKit(kit: Kit) {
        val oldKit = getKitById(kit.id)
        kitDao.update(kit)
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
                            productNumber = "${oldKit.batchSize + i}",
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

    suspend fun getProductsByKitId(id: Long): List<Product> {
        val batches = batchService.getBatchesByKitId(id)
        return productService.getProductsByBatches(batches)
    }
}