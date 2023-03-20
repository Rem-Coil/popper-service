package com.remcoil.service.action

import com.remcoil.dao.action.ActionDao
import com.remcoil.data.model.action.Action
import com.remcoil.data.model.action.BatchAction
import com.remcoil.service.product.ProductService
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

    suspend fun getActionById(id: Long): Action? {
        val action = actionDao.getById(id)
        logger.info("Отдали операцию с id=${id}")
        return action
    }

    suspend fun updateAction(action: Action) {
        if (productService.productIsActive(action.productId)) {
            actionDao.update(action)
            logger.info("Данные об операции с id=${action.id}")
        } else {
            throw InActiveProductException("Изделие с id=${action.productId} неактивно")
        }
    }

    suspend fun createAction(action: Action): Action {
        if (productService.productIsActive(action.productId)) {
            val createdAction = actionDao.create(action)
            logger.info("Операция с id=${createdAction.id} сохранена")
            return createdAction
        } else {
            throw InActiveProductException("Изделие с id=${action.productId} неактивно")
        }
    }

    suspend fun createBatchActions(batchAction: BatchAction, operatorId: Long): List<Action> {
        val products = productService.getProductsByBatchId(batchAction.batchId)
            .filter { bobbin -> bobbin.active }

        val actions = ArrayList<Action>()
        for (product in products) {
            actions.add(
                Action(
                    id = 0,
                    operatorId = operatorId,
                    productId = product.id,
                    actionTypeId = batchAction.actionType,
                    doneTime = batchAction.doneTime,
                    successful = batchAction.successful
                )
            )
        }

        return actionDao.createBatchAction(actions)
    }

    suspend fun deleteActionById(id: Long) {
        actionDao.deleteById(id)
        logger.info("Удалили данные об операции - $id")
    }

    suspend fun isActionUnsuccessful(actionId: Long): Boolean {
        val action = getActionById(actionId)
        return action != null && !action.successful
    }
}