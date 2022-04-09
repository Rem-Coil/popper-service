package com.remcoil.service.action

import com.remcoil.dao.action.ActionDao
import com.remcoil.data.model.action.Action
//import com.remcoil.data.model.action.ActionIdentity
import com.remcoil.data.model.action.FullAction
import com.remcoil.utils.logger

class ActionService(private val dao: ActionDao) {

    fun getAll(): List<Action> {
        val actions = dao.getAll()
        logger.info("Отдали все операции")
        return actions
    }

    fun getByTaskId(taskId: Int): List<FullAction> {
        val actions = dao.getByTaskId(taskId)
        logger.info("Отдали все операции по ТЗ - $taskId")
        return actions
    }

    fun getByBobbinId(bobbinId: Int): List<FullAction> {
        val actions = dao.getByBobbinId(bobbinId)
        logger.info("Отдали все операции по катушке - $bobbinId")
        return actions
    }

    suspend fun updateAction(action: Action) {
        dao.updateAction(action)
        logger.info("Данные об операции с id=${action.id}")
    }

    suspend fun createAction(action: Action): Action {
        val act = dao.createAction(action)
        logger.info("Запись с id=${act.id} сохранена")
        return act
    }

    fun deleteAction(actionId: Int) {
        dao.deleteAction(actionId)
        logger.info("Удалили данные об операции - $actionId")
    }
}