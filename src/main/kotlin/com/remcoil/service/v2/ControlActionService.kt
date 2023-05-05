package com.remcoil.service.v2

import com.remcoil.dao.v2.ControlActionDao
import com.remcoil.data.model.v2.ControlAction
import com.remcoil.data.model.v2.ExtendedControlAction
import com.remcoil.utils.exceptions.InActiveProductException
import com.remcoil.utils.logger

class ControlActionService(
    private val controlActionDao: ControlActionDao,
    private val productService: ProductService,
) {
    suspend fun getAllControlActions(): List<ControlAction> {
        return controlActionDao.getAll()
    }

    suspend fun getControlActionsByKitId(id: Long): List<ExtendedControlAction> {
        return controlActionDao.getByKitId(id)
    }

    suspend fun createControlAction(controlAction: ControlAction): ControlAction {
        if (productService.productIsActive(controlAction.productId)) {
            val createdControlAction = controlActionDao.create(controlAction)
            logger.info("Операция контроля с id=${createdControlAction.id} сохранена")
            return createdControlAction
        } else throw InActiveProductException("Изделие с id=${controlAction.productId} неактивно")
    }

    suspend fun updateControlAction(controlAction: ControlAction) {
        controlActionDao.update(controlAction)
        logger.info("Данные об операции контроля с id=${controlAction.id}")
    }

    suspend fun deleteControlAction(id: Long) {
        controlActionDao.deleteById(id)
        logger.info("Данные об операции контроля с id=${id} удалены")
    }
}