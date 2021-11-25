package com.remcoil.service.action

import com.remcoil.dao.action.ActionDao
import com.remcoil.data.model.action.Action
//import com.remcoil.data.model.action.ActionIdentity
import com.remcoil.data.model.action.FullAction

class ActionService(private val dao: ActionDao) {

    fun getAll(): List<Action> {
        return dao.getAll()
    }

    fun getByTaskId(taskId: Int): List<FullAction> {
        return dao.getByTaskId(taskId)
    }

    fun getByBobbinId(bobbinId: Int): List<FullAction> {
        return dao.getByBobbinId(bobbinId)
    }

    suspend fun updateAction(action: Action) {
        return dao.updateAction(action)
    }

    suspend fun createAction(action: Action) =
        if (dao.isNotExist(action)) dao.createAction(action) else null

    fun deleteAction(actionId: Int) {
        dao.deleteAction(actionId)
    }
}