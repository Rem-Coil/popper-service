package com.remcoil.service.v2

import com.remcoil.dao.v2.KitDao
import com.remcoil.data.model.v2.Kit
import com.remcoil.data.model.v2.KitProgress
import com.remcoil.utils.exceptions.EntryDoesNotExistException
import com.remcoil.utils.logger


class KitService(
    private val kitDao: KitDao,
    private val batchService: BatchService,
    private val operationTypeService: OperationTypeService
) {

    suspend fun getAllKits(): List<Kit> {
        val kits = kitDao.getAll()
        logger.info("Вернули все наборы")
        return kits
    }

    suspend fun getKitById(id: Long): Kit {
        val kit = kitDao.getById(id) ?: throw EntryDoesNotExistException("Набор с id = $id не существует")
        logger.info("Вернули данные от наборе - $id")
        return kit
    }

    suspend fun deleteKitById(id: Long) {
        kitDao.deleteById(id)
        logger.info("Данные о ТЗ удалены")
    }

    suspend fun createKit(kit: Kit): Kit {
        val created = kitDao.create(kit)
        logger.info("Создан набор - ${created.kitNumber}")
        batchService.createByKit(created, startNumber = 1)
        return created
    }

    suspend fun updateKit(kit: Kit) {
        val oldKit = getKitById(kit.id)
        kitDao.update(kit)

        batchService.updateBatchSize(oldKit, kit)
        batchService.updateBatchesQuantity(oldKit, kit)

        logger.info("Обновили набор с id = ${kit.id}")
    }

    suspend fun getKitProgressById(id: Long): KitProgress {
        val kit = getKitById(id)
        val operationTypes = operationTypeService.getOperationTypesBySpecificationId(kit.specificationId)
        val batchesProgress = batchService.getBatchesProgressByKitId(kit.id)

        return KitProgress(
            kit,
            operationTypes,
            batchesProgress
        )
    }
}