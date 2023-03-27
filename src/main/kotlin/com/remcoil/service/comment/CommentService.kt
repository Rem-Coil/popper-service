package com.remcoil.service.comment

import com.remcoil.dao.comment.CommentDao
import com.remcoil.data.model.comment.Comment
import com.remcoil.service.action.ActionService
import com.remcoil.utils.logger

class CommentService(
    private val commentDao: CommentDao,
    private val actionService: ActionService
) {
    suspend fun getCommentByActionId(actionId: Long): Comment? {
        val comment = commentDao.getByActionId(actionId)
        logger.info("Отдали комментарии для операции с id = $actionId")
        return comment
    }

    suspend fun createComment(comment: Comment): Comment? {
        return if (actionService.isUnsuccessful(comment.actionId)) {
            val createdComment = commentDao.createComment(comment)
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
        commentDao.updateComment(comment)
        logger.info("Обновили комментарий для операции с id = ${comment.actionId}")
    }

    suspend fun deleteComment(actionId: Long) {
        commentDao.deleteComment(actionId)
        logger.info("Удалили комментарий для операции с id = $actionId")
    }
}