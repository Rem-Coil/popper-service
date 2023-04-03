package com.remcoil.service.v2

import com.remcoil.dao.v2.ActionDao
import com.remcoil.data.model.action.BatchAction
import com.remcoil.data.model.v2.Action
import com.remcoil.utils.exceptions.InActiveProductException
import com.remcoil.utils.logger

class ActionService(
    private val actionDao: ActionDao,
    private val productService: ProductService
) {
    suspend fun getAllActions(): List<Action> {
        val actions = actionDao.getAll()
        logger.info("Отдали все операции")
        return actions
    }

    suspend fun getByProductId(id: Long): List<Action> {
        val actions = actionDao.getByProductId(id)
        logger.info("Отдали все операции по изделию - $id")
        return actions
    }

    suspend fun updateAction(action: Action) {
        if (!productService.productIsActive(action.productId)) {
            throw InActiveProductException("Изделие с id=${action.productId} неактивно")
        }

        actionDao.update(action)
        logger.info("Данные об операции с id=${action.id}")
    }

    suspend fun createAction(action: Action): Action {
        if (!productService.productIsActive(action.productId)) {
            throw InActiveProductException("Изделие с id=${action.productId} неактивно")
        }

        val createdAction = actionDao.create(action)
        logger.info("Операция с id=${createdAction.id} сохранена")
        return createdAction
    }

    suspend fun createBatchActions(batchAction: BatchAction, operatorId: Long): List<Action> {
        val products = productService.getProductsByBatchId(batchAction.batchId)
            .filter { bobbin -> bobbin.active }

        val actions = ArrayList<Action>()
        for (product in products) {
            actions.add(
                Action(
                    employeeId = operatorId,
                    productId = product.id,
                    action = batchAction,
                )
            )
        }

        return actionDao.batchCreate(actions)
    }

    suspend fun deleteActionById(id: Long) {
        actionDao.deleteById(id)
        logger.info("Удалили данные об операции - $id")
    }
}