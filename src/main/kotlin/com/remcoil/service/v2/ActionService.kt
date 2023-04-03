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
                    employeeId = operatorId,
                    productId = product.id,
                    actionType = batchAction.actionType,
                    doneTime = batchAction.doneTime,
                    successful = batchAction.successful
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