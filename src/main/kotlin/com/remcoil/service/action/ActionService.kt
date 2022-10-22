package com.remcoil.service.action

import com.remcoil.dao.action.ActionDao
import com.remcoil.data.model.action.Action
import com.remcoil.data.model.action.BatchAction
import com.remcoil.service.bobbin.BobbinService
import com.remcoil.utils.exceptions.InActiveBobbinException
import com.remcoil.utils.exceptions.WrongParamException
import com.remcoil.utils.logger

class ActionService(
    private val actionDao: ActionDao,
    private val bobbinService: BobbinService
) {
    suspend fun getAll(): List<Action> {
        val actions = actionDao.getAll()
        logger.info("Отдали все операции")
        return actions
    }

    suspend fun getByBobbinId(bobbinId: Long): List<Action> {
        val actions = actionDao.getByBobbinId(bobbinId)
        logger.info("Отдали все операции по катушке - $bobbinId")
        return actions
    }

    suspend fun getById(actionId: Long): Action? {
        val action = actionDao.getById(actionId)
        logger.info("Отдали операцию с id=${actionId}")
        return action
    }

    suspend fun updateAction(action: Action) {
        if (bobbinService.isActive(action.bobbinId)) {
            actionDao.updateAction(action)
            logger.info("Данные об операции с id=${action.id}")
        } else {
            throw InActiveBobbinException("Катушка с id=${action.bobbinId} неактивна")
        }
    }

    suspend fun updateType(actionId: Long, type: String) {
        val action = getById(actionId) ?: throw WrongParamException("Операция не найдена")
        if (bobbinService.isActive(action.bobbinId)) {
            actionDao.updateType(actionId, type)
            logger.info("Тип операции обновлен")
        } else {
            throw InActiveBobbinException("Катушка с id=${action.bobbinId} неактивна")
        }
    }

    suspend fun createAction(action: Action): Action {
        if (bobbinService.isActive(action.bobbinId)) {
            val act = actionDao.createAction(action)
            logger.info("Запись с id=${act.id} сохранена")
            return act
        } else {
            throw InActiveBobbinException("Катушка с id=${action.bobbinId} неактивна")
        }
    }

    suspend fun createBatchActions(batchAction: BatchAction, operatorId: Int): List<Action> {
        val bobbinIdList = bobbinService.getByBatchId(batchAction.batchId)
            .filter { bobbin -> bobbin.active }
            .map { bobbin -> bobbin.id }

        val actions = ArrayList<Action>()
        for (bobbinId in bobbinIdList) {
            actions.add(
                Action(
                    id = 0,
                    operatorId = operatorId,
                    bobbinId = bobbinId,
                    actionType = batchAction.actionType,
                    doneTime = batchAction.doneTime,
                    successful = batchAction.successful
                )
            )
        }

        return actionDao.createBatchAction(actions)
    }

    suspend fun deleteAction(actionId: Long) {
        actionDao.deleteAction(actionId)
        logger.info("Удалили данные об операции - $actionId")
    }

    suspend fun isUnsuccessful(actionId: Long): Boolean {
        val action = getById(actionId)
        return action != null && !action.successful
    }
}