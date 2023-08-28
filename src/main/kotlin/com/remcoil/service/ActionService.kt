package com.remcoil.service

import com.remcoil.dao.ActionDao
import com.remcoil.model.dto.Action
import com.remcoil.model.dto.BatchActionRequest
import com.remcoil.model.dto.ExtendedAction
import com.remcoil.utils.exceptions.InActiveProductException
import com.remcoil.utils.exceptions.LockedProductException
import com.remcoil.utils.exceptions.UnLockedProductException
import com.remcoil.utils.exceptions.UnsupportedOperationTypeException

class ActionService(
    private val actionDao: ActionDao,
    private val controlActionService: ControlActionService,
    private val productService: ProductService
) {
    suspend fun getAllActions(): List<Action> {
        return actionDao.getAll()
    }

    suspend fun getActionsBySpecificationId(id: Long): List<ExtendedAction> {
        return actionDao.getBySpecificationId(id)
    }

    suspend fun getActionsByKitId(id: Long): List<ExtendedAction> {
        return actionDao.getByKitId(id)
    }

    suspend fun getActionsByProductId(id: Long): List<Action> {
        return actionDao.getByProductId(id)
    }

    suspend fun updateAction(action: Action) {
        actionDao.update(action)
    }

    suspend fun createAction(action: Action): Action {
        val product = productService.getProductById(action.productId)
        if (!product.active) {
            throw InActiveProductException("Product with id = ${product.id} inactive")
        }
        if (product.locked && !action.repair) {
            throw LockedProductException("Product with id = ${product.id} is locked")
        }
        if (action.repair) {
            if (!product.locked) {
                throw UnLockedProductException("Product with id = ${product.id} is unlocked")
            } else {
                val lastControlAction =
                    controlActionService.getControlActionsByProductId(action.productId).filter { it.needRepair }
                        .maxBy { it.doneTime }
                if (lastControlAction.operationType == action.operationType) {
                    val createdAction = actionDao.create(action)
                    productService.setLockValueByProductId(product.id, false)
                    return createdAction
                } else {
                    throw LockedProductException("Product with id = ${product.id} is locked due to another operation type")
                }
            }
        } else {
            return actionDao.create(action)
        }
    }

    suspend fun createBatchActions(batchActionRequest: BatchActionRequest, employeeId: Long): List<Action> {
        if (batchActionRequest.repair) {
            throw UnsupportedOperationTypeException("Repair operations are not supported for batch actions")
        }
        val validProductsId = productService.getProductsByIdList(batchActionRequest.productsId)
            .filter { it.active && !it.locked }
            .map { it.id }

        return actionDao.batchCreate(batchActionRequest.toActions(employeeId, validProductsId))
    }

    suspend fun deleteActionById(id: Long) {
        actionDao.deleteById(id)
    }

    suspend fun deleteActionsByProducts(productsIdList: List<Long>) {
        actionDao.deleteByProducts(productsIdList)
    }
}