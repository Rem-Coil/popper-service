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
    suspend fun getAll(): List<ControlAction> {
        return controlActionDao.getAll()
    }

    suspend fun getBySpecificationId(id: Long): List<ExtendedControlAction> {
        return controlActionDao.getBySpecificationId(id)
    }

    suspend fun getByKitId(id: Long): List<ExtendedControlAction> {
        return controlActionDao.getByKitId(id)
    }

    suspend fun getByProductId(id: Long): List<ControlAction> {
        return controlActionDao.getByProductId(id)
    }

    suspend fun batchCreate(controlActions: List<ControlAction>): List<ControlAction> {
        val validProductsId = productService.getProductsByIdList(controlActions.map { it.productId })
            .filter { it.active && !it.locked }
            .map { it.id }
        if (controlActions.first().needRepair) {
            productService.setLockValueByIdList(validProductsId, true)
        }
        return controlActionDao.batchCreate(controlActions.filter { it.productId in validProductsId })
    }

    suspend fun create(controlAction: ControlAction): ControlAction {
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