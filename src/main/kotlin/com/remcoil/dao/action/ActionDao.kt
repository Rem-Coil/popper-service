package com.remcoil.dao.action

import com.remcoil.data.database.Actions
import com.remcoil.data.model.action.Action
import com.remcoil.utils.safetySuspendTransactionAsync
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class ActionDao(private val database: Database) {

    suspend fun getAll(): List<Action> = safetySuspendTransactionAsync(database) {
        Actions
            .selectAll()
            .map(::extractAction)
    }

    suspend fun getById(id: Long) = safetySuspendTransactionAsync(database) {
        Actions
            .select { Actions.id eq id }
            .map(::extractAction)
            .firstOrNull()
    }

    suspend fun getByProductId(id: Long): List<Action> = safetySuspendTransactionAsync(database) {
        Actions
            .select { Actions.productId eq id }
            .map(::extractAction)
    }

    suspend fun update(action: Action) = safetySuspendTransactionAsync(database) {
        Actions.update({ Actions.id eq action.id }) {
            it[employeeId] = action.employeeId
            it[productId] = action.productId
            it[actionTypeId] = action.actionTypeId
            it[doneTime] = action.doneTime.toJavaLocalDateTime()
            it[successful] = action.successful
        }
    }

    suspend fun create(action: Action): Action = safetySuspendTransactionAsync(database) {
        val id = Actions.insertAndGetId {
            it[employeeId] = action.employeeId
            it[productId] = action.productId
            it[actionTypeId] = action.actionTypeId
            it[doneTime] = action.doneTime.toJavaLocalDateTime()
            it[successful] = action.successful
        }
        action.copy(id = id.value)
    }

    suspend fun createBatchAction(actions: List<Action>) = safetySuspendTransactionAsync(database) {
        Actions.batchInsert(actions) { action: Action ->
            this[Actions.employeeId] = action.employeeId
            this[Actions.productId] = action.productId
            this[Actions.actionTypeId] = action.actionTypeId
            this[Actions.doneTime] = action.doneTime.toJavaLocalDateTime()
            this[Actions.successful] = action.successful
        }
            .map(::extractAction)
    }

    suspend fun deleteById(id: Long) = safetySuspendTransactionAsync(database) {
        Actions.deleteWhere { Actions.id eq id }
    }

    private fun extractAction(row: ResultRow): Action = Action(
        row[Actions.id].value,
        row[Actions.employeeId].value,
        row[Actions.productId].value,
        row[Actions.actionTypeId].value,
        row[Actions.doneTime].toKotlinLocalDateTime(),
        row[Actions.successful]
    )
}