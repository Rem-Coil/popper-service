package com.remcoil.dao.comment

import com.remcoil.data.database.DefectComments
import com.remcoil.data.model.comment.Comment
import com.remcoil.utils.safetySuspendTransactionAsync
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq


class CommentDao(private val database: Database) {

    suspend fun getAll(): List<Comment> = safetySuspendTransactionAsync(database) {
        DefectComments
            .selectAll()
            .map(::extractDefectsComment)
    }

    suspend fun getByActionId(actionId: Long) = safetySuspendTransactionAsync(database) {
        DefectComments
            .select {DefectComments.actionId eq actionId}
            .map(::extractDefectsComment)
            .firstOrNull()
    }

    suspend fun createComment(comment: Comment) = safetySuspendTransactionAsync(database) {
        val insert = DefectComments
            .insert {
                it[actionId] = comment.actionId
                it[DefectComments.comment] = comment.comment
            }
        comment.copy(actionId = insert[DefectComments.actionId].value)
    }

    suspend fun deleteComment(actionId: Long) = safetySuspendTransactionAsync(database) {
        DefectComments
            .deleteWhere { DefectComments.actionId eq actionId }
    }

    suspend fun updateComment(comment: Comment) = safetySuspendTransactionAsync(database) {
        DefectComments
            .update({DefectComments.actionId eq comment.actionId}) {
                it[DefectComments.comment] = comment.comment
            }
    }

    private fun extractDefectsComment(row: ResultRow): Comment = Comment(
        row[DefectComments.actionId].value,
        row[DefectComments.comment]
    )
}