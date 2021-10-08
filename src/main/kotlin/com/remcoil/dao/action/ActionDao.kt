package com.remcoil.dao.action

import com.remcoil.data.database.Actions
import com.remcoil.data.database.Bobbins
import com.remcoil.data.database.Tasks
import com.remcoil.data.model.action.Action
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

    fun createAction(action: Action): Action = transaction(database) {
        val id = Actions.insertAndGetId {
            it[operatorId] = action.operatorId
            it[bobbinId] = action.bobbinId
            it[actionType] = action.actionType
            it[doneTime] = action.doneTime.toJavaLocalDateTime()
        }
        updateTask(action, 1)
        action.copy(id = id.value)
    }

    fun deleteAction(id: Int) = transaction(database) {
        val action = Actions
            .select{Actions.id eq id}
            .map(::extractAction)
            .first()
        updateTask(action, -1)
        Actions.deleteWhere { Actions.id eq id }
    }

    private fun updateTask(action: Action, add: Int) {
        val id = Bobbins
            .slice(Bobbins.taskId)
            .select { Bobbins.id eq action.bobbinId }
            .map(::extractTaskId)
            .first()
        Tasks.update({ Tasks.id eq id }) {
            with(SqlExpressionBuilder) {
                when (action.actionType) {
                    "winding" -> it.update(winding, winding + add)
                    "output" -> it.update(output, output + add)
                    "isolation" -> it.update(isolation, isolation + add)
                    "molding" -> it.update(molding, molding + add)
                    "crimping" -> it.update(crimping, crimping + add)
                    "quality" -> it.update(quality, quality + add)
                    "testing" -> it.update(testing, testing + add)
                }
            }
        }
    }

    private fun extractTaskId(row: ResultRow): Int = row[Bobbins.taskId].value
    private fun extractAction(row: ResultRow): Action = Action(
        row[Actions.id].value,
        row[Actions.operatorId].value,
        row[Actions.bobbinId].value,
        row[Actions.actionType],
        row[Actions.doneTime].toKotlinLocalDateTime()
    )
}