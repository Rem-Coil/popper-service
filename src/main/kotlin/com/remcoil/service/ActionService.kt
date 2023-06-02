package com.remcoil.service

import com.remcoil.dao.ActionDao
import com.remcoil.utils.exceptions.InActiveProductException

class ActionService(
    private val actionDao: ActionDao,
    private val productService: ProductService
) {
    suspend fun getAllActions(): List<com.remcoil.model.dto.Action> {
        return actionDao.getAll()
    }

    suspend fun getActionsBySpecificationId(id: Long): List<com.remcoil.model.dto.ExtendedAction> {
        return actionDao.getBySpecificationId(id)
    }

    suspend fun getActionsByKitId(id: Long): List<com.remcoil.model.dto.ExtendedAction> {
        return actionDao.getByKitId(id)
    }

    suspend fun getActionsByProductId(id: Long): List<com.remcoil.model.dto.Action> {
        return actionDao.getByProductId(id)
    }

    suspend fun updateAction(action: com.remcoil.model.dto.Action) {
        actionDao.update(action)
    }

    suspend fun createAction(action: com.remcoil.model.dto.Action): com.remcoil.model.dto.Action {
        if (productService.productIsActive(action.productId)) {
            return actionDao.create(action)
        } else throw InActiveProductException("Product with id = ${action.productId} inactive")
    }

    suspend fun createBatchActions(
        batchActionRequest: com.remcoil.model.dto.BatchActionRequest,
        employeeId: Long
    ): List<com.remcoil.model.dto.Action> {
        val products = productService.getProductsByBatchId(batchActionRequest.batchId)
            .filter { product -> product.active }

        val actions = ArrayList<com.remcoil.model.dto.Action>()
        for (product in products) {
            actions.add(batchActionRequest.toAction(employeeId, product.id))
        }

        return actionDao.batchCreate(actions)
    }

    suspend fun deleteActionById(id: Long) {
        actionDao.deleteById(id)
    }

    suspend fun deleteActionsByProducts(productsIdList: List<Long>) {
        actionDao.deleteByProducts(productsIdList)
    }
}