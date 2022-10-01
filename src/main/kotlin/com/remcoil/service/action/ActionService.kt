package com.remcoil.service.action

import com.remcoil.dao.action.ActionDao
import com.remcoil.dao.action.FullActionDao
import com.remcoil.data.model.action.Action
import com.remcoil.data.model.action.full.FullAction
import com.remcoil.service.bobbin.BobbinService
import com.remcoil.utils.exceptions.InActiveBobbinException
import com.remcoil.utils.exceptions.WrongParamException
import com.remcoil.utils.logger

class ActionService(
    private val actionDao: ActionDao,
    private val fullActionDao: FullActionDao,
    private val bobbinService: BobbinService
) {
    suspend fun getAllFull(): List<FullAction> {
        val actions = fullActionDao.getAll()
        logger.info("Отдали все операции")
        return actions
    }

    suspend fun getFullByBatchId(batchId: Long): List<FullAction> {
        val actions = fullActionDao.getByBatchId(batchId)
        logger.info("Отдали все операции по ТЗ - $batchId")
        return actions
    }

    suspend fun getFullByTaskId(taskId: Int): List<FullAction> {
        val actions = fullActionDao.getByTaskId(taskId)
        logger.info("Отдали все операции по ТЗ - $taskId")
        return actions
    }

    suspend fun getFullByBobbinId(bobbinId: Long): List<FullAction> {
        val actions = fullActionDao.getByBobbinId(bobbinId)
        logger.info("Отдали все операции по катушке с id = $bobbinId")
        return actions
    }

    suspend fun getFullById(id: Long): FullAction? {
        val actions = fullActionDao.getById(id)
        logger.info("Отдали операцию с id - $id")
        return actions
    }

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

    suspend fun deleteAction(actionId: Long) {
        actionDao.deleteAction(actionId)
        logger.info("Удалили данные об операции - $actionId")
    }

    suspend fun isUnsuccessful(actionId: Long): Boolean {
        val action = getById(actionId)
        return action != null && !action.successful
    }
}