package com.remcoil.service

import com.remcoil.dao.ActionDao
import com.remcoil.utils.exceptions.InActiveProductException
import com.remcoil.utils.logger

class ActionService(
    private val actionDao: ActionDao,
    private val productService: ProductService
) {
    suspend fun getAllActions(): List<com.remcoil.model.dto.Action> {
        val actions = actionDao.getAll()
        logger.info("Отдали все операции")
        return actions
    }

    suspend fun getActionsBySpecificationId(id: Long): List<com.remcoil.model.dto.ExtendedAction> {
        return actionDao.getBySpecificationId(id)
    }

    suspend fun getActionsByKitId(id: Long): List<com.remcoil.model.dto.ExtendedAction> {
        return actionDao.getByKitId(id)
    }

    suspend fun getActionsByProductId(id: Long): List<com.remcoil.model.dto.Action> {
        val actions = actionDao.getByProductId(id)
        logger.info("Отдали все операции по изделию - $id")
        return actions
    }

    suspend fun updateAction(action: com.remcoil.model.dto.Action) {
        actionDao.update(action)
        logger.info("Данные об операции с id=${action.id}")
    }

    suspend fun createAction(action: com.remcoil.model.dto.Action): com.remcoil.model.dto.Action {
        if (productService.productIsActive(action.productId)) {
            val createdAction = actionDao.create(action)
            logger.info("Операция с id=${createdAction.id} сохранена")
            return createdAction
        } else throw InActiveProductException("Изделие с id=${action.productId} неактивно")
    }

    suspend fun createBatchActions(batchActionRequest: com.remcoil.model.dto.BatchActionRequest, employeeId: Long): List<com.remcoil.model.dto.Action> {
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
        logger.info("Удалили данные об операции - $id")
    }
}