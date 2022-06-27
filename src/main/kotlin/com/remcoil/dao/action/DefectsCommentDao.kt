package com.remcoil.dao.action

import com.remcoil.data.database.DefectsComments
import com.remcoil.data.model.action.DefectsComment
import com.remcoil.utils.safetySuspendTransactionAsync
import org.jetbrains.exposed.sql.*


class DefectsCommentDao(private val database: Database) {

    suspend fun getAll(): List<DefectsComment> = safetySuspendTransactionAsync(database) {
        DefectsComments
            .selectAll()
            .map(::extractDefectsComment)
    }

    suspend fun getByActionId(actionId: Long) = safetySuspendTransactionAsync(database) {
        DefectsComments
            .select {DefectsComments.actionId eq actionId}
            .map(::extractDefectsComment)
            .firstOrNull()
    }

    suspend fun createComment(comment: DefectsComment) = safetySuspendTransactionAsync(database) {
        val insert = DefectsComments
            .insert {
                it[DefectsComments.actionId] = comment.actionId
                it[DefectsComments.comment] = comment.comment
            }
        comment.copy(actionId = insert[DefectsComments.actionId].value)
    }

    suspend fun deleteComment(actionId: Long) = safetySuspendTransactionAsync(database) {
        DefectsComments
            .deleteWhere { DefectsComments.actionId eq actionId }
    }

    suspend fun updateComment(comment: DefectsComment) = safetySuspendTransactionAsync(database) {
        DefectsComments
            .update({DefectsComments.actionId eq comment.actionId}) {
                it[DefectsComments.comment] = comment.comment
            }
    }

    private fun extractDefectsComment(row: ResultRow): DefectsComment = DefectsComment(
        row[DefectsComments.actionId].value,
        row[DefectsComments.comment]
    )
}