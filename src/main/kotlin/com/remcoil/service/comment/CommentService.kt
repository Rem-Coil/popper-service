package com.remcoil.service.comment

import com.remcoil.dao.comment.CommentDao
import com.remcoil.data.model.comment.Comment
import com.remcoil.service.action.ActionService
import com.remcoil.utils.logger

class CommentService(
    private val commentDao: CommentDao,
    private val actionService: ActionService
) {
    suspend fun getComment(actionId: Long): Comment? {
        val comment = commentDao.getByActionId(actionId)
        logger.info("Отдали комментарии для операции с id = $actionId")
        return comment
    }

    suspend fun createComment(comment: Comment): Comment? {
        return if (actionService.isActionUnsuccessful(comment.actionId)) {
            val createdComment = commentDao.create(comment)
            logger.info("Сохранили комментарий для операции с id = ${comment.actionId}")
            createdComment
        } else {
            null
        }
    }

    suspend fun getAllComments(): List<Comment> {
        val comments = commentDao.getAll()
        logger.info("Отдали все комментарии")
        return comments
    }

    suspend fun updateComment(comment: Comment) {
        commentDao.update(comment)
        logger.info("Обновили комментарий для операции с id = ${comment.actionId}")
    }

    suspend fun deleteComment(actionId: Long) {
        commentDao.deleteByActionId(actionId)
        logger.info("Удалили комментарий для операции с id = $actionId")
    }
}