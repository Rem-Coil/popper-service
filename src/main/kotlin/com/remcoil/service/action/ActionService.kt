package com.remcoil.service.action

import com.remcoil.dao.action.ActionDao
import com.remcoil.data.model.action.Action
import com.remcoil.data.model.action.ActionIdentity
import com.remcoil.data.model.action.FullAction

class ActionService(private val dao: ActionDao) {

    fun getAll(): List<Action> {
        return dao.getAll()
    }

    fun getById(taskId: Int): List<FullAction> {
        return dao.getById(taskId)
    }

    fun createAction(actionIdentity: ActionIdentity): Action? {
        val action = Action(actionIdentity)
        if (dao.checkAction(action) != null) {
            return null
        }
        return dao.createAction(action)
    }

    fun deleteAction(actionId: Int) {
        dao.deleteAction(actionId)
    }
}