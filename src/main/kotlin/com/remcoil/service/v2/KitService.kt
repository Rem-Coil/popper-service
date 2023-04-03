package com.remcoil.service.v2

import com.remcoil.dao.v2.KitDao
import com.remcoil.data.model.v2.Kit
import com.remcoil.utils.exceptions.EntryDoesNotExistException
import com.remcoil.utils.logger


class KitService(
    private val kitDao: KitDao,
    private val batchService: BatchService,
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
        logger.info("Создан набор - ${createdKit.kitNumber}")
        batchService.createByKit(kit, startNumber = 1)
        return createdKit
    }

    suspend fun updateKit(kit: Kit) {
        val oldKit = getKitById(kit.id)
        kitDao.update(kit)
        logger.info("Обновили набор с id = ${kit.id}")
        batchService.updateBatchesQuantity(oldKit, kit)
        batchService.updateBatchSize(oldKit, kit)
    }
}