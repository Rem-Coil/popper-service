package com.remcoil.service.action

import com.remcoil.dao.action.ActionDao
import com.remcoil.dao.action.DefectsCommentDao
import com.remcoil.dao.action.FullActionDao
import com.remcoil.data.model.action.Action
import com.remcoil.data.model.action.ActionWithComment
import com.remcoil.data.model.action.DefectsComment
import com.remcoil.data.model.action.FullAction
import com.remcoil.utils.logger

class ActionService(private val actionDao: ActionDao,
                    private val fullActionDao: FullActionDao,
                    private val defectsCommentDao: DefectsCommentDao) {
    suspend fun getAllComments(): List<DefectsComment> {
        val comments = defectsCommentDao.getAll()
        logger.info("Отдали все комментарии")
        return comments
    }

    suspend fun getCommentByActionId(actionId: Long): DefectsComment? {
        val comment = defectsCommentDao.getByActionId(actionId)
        logger.info("Отдали комментарии для операции с id = $actionId")
        return comment
    }

    suspend fun createWithComment(actionWithComment: ActionWithComment): ActionWithComment {
        val action = createAction(Action(actionWithComment))
        defectsCommentDao.createComment(DefectsComment(action.id, actionWithComment.comment))
        logger.info("Сохранили комментарий для операции с id = ${action.id}")
        return actionWithComment.copy(id = action.id)
    }

    suspend fun updateComment(comment: DefectsComment) {
        defectsCommentDao.updateComment(comment)
        logger.info("Обновили комментарий для операции с id = ${comment.actionId}")
    }

    suspend fun deleteComment(actionId: Long) {
        defectsCommentDao.deleteComment(actionId)
        logger.info("Удалили комментарий для операции с id = $actionId")
    }

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

    suspend fun updateAction(action: Action) {
        actionDao.updateAction(action)
        logger.info("Данные об операции с id=${action.id}")
    }

    suspend fun createAction(action: Action): Action {
        val act = actionDao.createAction(action)
        logger.info("Запись с id=${act.id} сохранена")
        return act
    }

    suspend fun deleteAction(actionId: Long) {
        actionDao.deleteAction(actionId)
        logger.info("Удалили данные об операции - $actionId")
    }
}