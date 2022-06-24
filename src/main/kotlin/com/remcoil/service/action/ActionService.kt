package com.remcoil.service.action

import com.remcoil.dao.action.ActionDao
import com.remcoil.dao.action.FullActionDao
import com.remcoil.data.model.action.Action
import com.remcoil.data.model.action.FullAction
import com.remcoil.utils.logger

class ActionService(private val actionDao: ActionDao,
                    private val fullActionDao: FullActionDao) {

    fun getAllFull(): List<FullAction> {
        val actions = fullActionDao.getAll()
        logger.info("Отдали все операции")
        return actions
    }

    fun getFullByBatchId(batchId: Long): List<FullAction> {
        val actions = fullActionDao.getByBatchId(batchId)
        logger.info("Отдали все операции по ТЗ - $batchId")
        return actions
    }

    fun getFullByTaskId(taskId: Int): List<FullAction> {
        val actions = fullActionDao.getByTaskId(taskId)
        logger.info("Отдали все операции по ТЗ - $taskId")
        return actions
    }

    fun getFullByBobbinId(bobbinId: Int): List<FullAction> {
        val actions = fullActionDao.getByBobbinId(bobbinId)
        logger.info("Отдали все операции по катушке с id = $bobbinId")
        return actions
    }

    fun getFullById(id: Int): FullAction? {
        val actions = fullActionDao.getById(id)
        logger.info("Отдали операцию с id - $id")
        return actions
    }

    fun getAll(): List<Action> {
        val actions = actionDao.getAll()
        logger.info("Отдали все операции")
        return actions
    }

    suspend fun getByBobbinId(bobbinId: Int): List<Action> {
        val actions = actionDao.getByBobbinId(bobbinId)
        logger.info("Отдали все операции по катушке - $bobbinId")
        return actions
    }

    suspend fun updateAction(action: Action) {
        actionDao.updateAction(action)
        logger.info("Данные об операции с id=${action.id}")
    }

    suspend fun createAction(action: Action): Action {
        val act = actionDao.createAction(action)
        logger.info("Запись с id=${act.id} сохранена")
        return act
    }

    fun deleteAction(actionId: Int) {
        actionDao.deleteAction(actionId)
        logger.info("Удалили данные об операции - $actionId")
    }
}