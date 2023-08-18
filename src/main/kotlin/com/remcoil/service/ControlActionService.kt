package com.remcoil.service

import com.remcoil.dao.ControlActionDao
import com.remcoil.model.dto.ControlAction
import com.remcoil.model.dto.ExtendedControlAction
import com.remcoil.utils.exceptions.InActiveProductException
import com.remcoil.utils.exceptions.LockedProductException

class ControlActionService(
    private val controlActionDao: ControlActionDao,
    private val productService: ProductService,
) {
    suspend fun getAllControlActions(): List<ControlAction> {
        return controlActionDao.getAll()
    }

    suspend fun getControlActionsBySpecificationId(id: Long): List<ExtendedControlAction> {
        return controlActionDao.getBySpecificationId(id)
    }

    suspend fun getControlActionsByKitId(id: Long): List<ExtendedControlAction> {
        return controlActionDao.getByKitId(id)
    }

    suspend fun getControlActionsByProductId(id: Long): List<ControlAction> {
        return controlActionDao.getByProductId(id)
    }

    suspend fun createControlAction(controlAction: ControlAction): ControlAction {
        val product = productService.getProductById(controlAction.productId)
        if (!product.active) {
            throw InActiveProductException("Product with id = ${product.id} inactive")
        }
        if (product.locked) {
            throw LockedProductException("Product with id = ${product.id} is locked")
        }
        if (controlAction.needRepair) {
            productService.setLockValueByProductId(product.id, true)
        }
        return controlActionDao.create(controlAction)
    }

    suspend fun updateControlAction(controlAction: ControlAction) {
        controlActionDao.update(controlAction)
    }

    suspend fun deleteControlAction(id: Long) {
        controlActionDao.deleteById(id)
    }

    suspend fun deleteControlActionsByProducts(productsIdList: List<Long>) {
        controlActionDao.deleteByProducts(productsIdList)
    }
}