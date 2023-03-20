package com.remcoil.dao.comment

import com.remcoil.data.database.Comments
import com.remcoil.data.model.comment.Comment
import com.remcoil.utils.safetySuspendTransactionAsync
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq


class CommentDao(private val database: Database) {

    suspend fun getAll(): List<Comment> = safetySuspendTransactionAsync(database) {
        Comments
            .selectAll()
            .map(::extractComment)
    }

    suspend fun getByActionId(id: Long) = safetySuspendTransactionAsync(database) {
        Comments
            .select { Comments.actionId eq id }
            .map(::extractComment)
            .firstOrNull()
    }

    suspend fun create(comment: Comment) = safetySuspendTransactionAsync(database) {
        val insert = Comments.insert {
            it[actionId] = comment.actionId
            it[Comments.comment] = comment.comment
        }
        comment.copy(actionId = insert[Comments.actionId].value)
    }

    suspend fun deleteByActionId(id: Long) = safetySuspendTransactionAsync(database) {
        Comments.deleteWhere { actionId eq id }
    }

    suspend fun update(comment: Comment) = safetySuspendTransactionAsync(database) {
        Comments.update({ Comments.actionId eq comment.actionId }) {
            it[Comments.comment] = comment.comment
        }
    }

    private fun extractComment(row: ResultRow): Comment = Comment(
        row[Comments.actionId].value,
        row[Comments.comment]
    )
}