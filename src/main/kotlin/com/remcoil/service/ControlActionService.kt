package com.remcoil.service

import com.remcoil.dao.ControlActionDao
import com.remcoil.utils.exceptions.InActiveProductException
import com.remcoil.utils.logger

class ControlActionService(
    private val controlActionDao: ControlActionDao,
    private val productService: ProductService,
) {
    suspend fun getAllControlActions(): List<com.remcoil.model.dto.ControlAction> {
        return controlActionDao.getAll()
    }

    suspend fun getControlActionsBySpecificationId(id: Long): List<com.remcoil.model.dto.ExtendedControlAction> {
        return controlActionDao.getBySpecificationId(id)
    }

    suspend fun getControlActionsByKitId(id: Long): List<com.remcoil.model.dto.ExtendedControlAction> {
        return controlActionDao.getByKitId(id)
    }

    suspend fun getControlActionsByProductId(id: Long): List<com.remcoil.model.dto.ControlAction> {
        return controlActionDao.getByProductId(id)
    }

    suspend fun createControlAction(controlAction: com.remcoil.model.dto.ControlAction): com.remcoil.model.dto.ControlAction {
        if (productService.productIsActive(controlAction.productId)) {
            val createdControlAction = controlActionDao.create(controlAction)
            logger.info("Операция контроля с id=${createdControlAction.id} сохранена")
            return createdControlAction
        } else throw InActiveProductException("Изделие с id=${controlAction.productId} неактивно")
    }

    suspend fun updateControlAction(controlAction: com.remcoil.model.dto.ControlAction) {
        controlActionDao.update(controlAction)
        logger.info("Данные об операции контроля с id=${controlAction.id}")
    }

    suspend fun deleteControlAction(id: Long) {
        controlActionDao.deleteById(id)
        logger.info("Данные об операции контроля с id=${id} удалены")
    }
}