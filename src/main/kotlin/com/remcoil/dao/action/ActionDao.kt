package com.remcoil.dao.action

import com.remcoil.data.database.*
import com.remcoil.data.model.action.Action
import com.remcoil.utils.safetySuspendTransactionAsync
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import org.jetbrains.exposed.sql.*

class ActionDao(private val database: Database) {

    suspend fun getAll(): List<Action> = safetySuspendTransactionAsync(database) {
        Actions
            .selectAll()
            .map(::extractAction)
    }

    suspend fun getById(id: Long) = safetySuspendTransactionAsync(database) {
        Actions
            .select { Actions.id eq id }
            .map (::extractAction)
            .firstOrNull()
    }

    suspend fun getByBobbinId(id: Long): List<Action> = safetySuspendTransactionAsync(database) {
        Actions
            .select { Actions.bobbinId eq id }
            .map(::extractAction)
    }

    suspend fun updateAction(action: Action) = safetySuspendTransactionAsync(database) {
        Actions.update({ Actions.id eq action.id }) {
            it[operatorId] = action.operatorId
            it[bobbinId] = action.bobbinId
            it[actionType] = action.actionType
            it[doneTime] = action.doneTime.toJavaLocalDateTime()
            it[successful] = action.successful
        }
    }

    suspend fun createAction(action: Action): Action = safetySuspendTransactionAsync(database) {
        val id = Actions.insertAndGetId {
            it[operatorId] = action.operatorId
            it[bobbinId] = action.bobbinId
            it[actionType] = action.actionType
            it[doneTime] = action.doneTime.toJavaLocalDateTime()
            it[successful] = action.successful
        }
        action.copy(id = id.value)
    }

    suspend fun deleteAction(id: Long) = safetySuspendTransactionAsync(database) {
        Actions.deleteWhere { Actions.id eq id }
    }

    private fun extractAction(row: ResultRow): Action = Action(
        row[Actions.id].value,
        row[Actions.operatorId].value,
        row[Actions.bobbinId].value,
        row[Actions.actionType],
        row[Actions.doneTime].toKotlinLocalDateTime(),
        row[Actions.successful]
    )

    suspend fun updateSuccess(actionId: Long, success: Boolean) = safetySuspendTransactionAsync(database){
        Actions
            .update({Actions.id eq actionId}) {
                it[successful] = success
            }
    }
}