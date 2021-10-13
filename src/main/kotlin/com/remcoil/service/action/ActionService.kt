package com.remcoil.service.action

import com.remcoil.dao.action.ActionDao
import com.remcoil.data.model.action.Action
import com.remcoil.data.model.action.ActionIdentity

class ActionService(private val dao: ActionDao) {

    fun getAll(): List<Action> {
        return dao.getAll()
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