package com.remcoil.service

import com.remcoil.dao.ControlActionDao
import com.remcoil.utils.exceptions.InActiveProductException

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
            return controlActionDao.create(controlAction)
        } else throw InActiveProductException("Product with id = ${controlAction.productId} inactive")
    }

    suspend fun updateControlAction(controlAction: com.remcoil.model.dto.ControlAction) {
        controlActionDao.update(controlAction)
    }

    suspend fun deleteControlAction(id: Long) {
        controlActionDao.deleteById(id)
    }

    suspend fun deleteControlActionsByProducts(productsIdList : List<Long>) {
        controlActionDao.deleteByProducts(productsIdList)
    }
}