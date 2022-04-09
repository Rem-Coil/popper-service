package com.remcoil.dao.action

import com.remcoil.data.database.Actions
import com.remcoil.data.database.Bobbins
import com.remcoil.data.database.Operators
import com.remcoil.data.database.Tasks
import com.remcoil.data.model.action.Action
import com.remcoil.data.model.action.FullAction
import com.remcoil.utils.safetySuspendTransactionAsync
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class ActionDao(private val database: Database) {

    fun getAll(): List<Action> = transaction(database) {
        Actions
            .selectAll()
            .map(::extractAction)
    }

    fun getByTaskId(id: Int): List<FullAction> = transaction(database) {
        (Actions innerJoin Operators innerJoin Bobbins innerJoin Tasks)
            .slice(
                Tasks.id, Tasks.taskName, Tasks.taskNumber,
                Bobbins.id, Bobbins.bobbinNumber,
                Operators.firstName, Operators.secondName, Operators.surname,
                Actions.actionType, Actions.doneTime, Actions.successful
            )
            .select { Tasks.id eq id }
            .map(::extractFullAction)
    }

    fun getByBobbinId(id: Int): List<FullAction> = transaction(database) {
        (Actions innerJoin Operators innerJoin Bobbins innerJoin Tasks)
            .slice(
                Tasks.id, Tasks.taskName, Tasks.taskNumber,
                Bobbins.id, Bobbins.bobbinNumber,
                Operators.firstName, Operators.secondName, Operators.surname,
                Actions.actionType, Actions.doneTime, Actions.successful
            )
            .select { Actions.bobbinId eq id }
            .map(::extractFullAction)
    }

    fun isNotExist(action: Action): Boolean = transaction(database) {
        Actions
            .select {
                (Actions.operatorId eq action.operatorId) and
                        (Actions.bobbinId eq action.bobbinId) and
                        (Actions.actionType eq action.actionType) and
                        (Actions.successful eq action.successful)
            }
            .map(::extractAction)
            .isNullOrEmpty()
    }

    fun isNotFix(action: Action): Boolean = transaction(database) {
        Actions
            .select {
                (Actions.bobbinId eq action.bobbinId) and
                        (Actions.actionType eq action.actionType) and
                        (Actions.successful neq action.successful)
            }
            .map(::extractAction)
            .isNullOrEmpty()
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

    fun deleteAction(id: Int) = transaction(database) {
        Actions.deleteWhere { Actions.id eq id }
    }

    private fun extractFullAction(row: ResultRow): FullAction = FullAction(
        row[Bobbins.id].value,
        row[Bobbins.bobbinNumber],
        row[Operators.firstName],
        row[Operators.secondName],
        row[Operators.surname],
        row[Actions.actionType],
        row[Actions.doneTime].toKotlinLocalDateTime(),
        row[Actions.successful]
    )

    private fun extractAction(row: ResultRow): Action = Action(
        row[Actions.id].value,
        row[Actions.operatorId].value,
        row[Actions.bobbinId].value,
        row[Actions.actionType],
        row[Actions.doneTime].toKotlinLocalDateTime(),
        row[Actions.successful]
    )
}