package com.remcoil.dao.v2

import com.remcoil.data.database.v2.Actions
import com.remcoil.data.model.v2.Action
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
            it[doneTime] = action.doneTime.toJavaLocalDateTime()
            it[repair] = action.repair
            it[operationType] = action.operationType
            it[employeeId] = action.employeeId.toInt()
            it[productId] = action.productId
        }
    }

    suspend fun create(action: Action): Action = safetySuspendTransactionAsync(database) {
        val id = Actions.insertAndGetId {
            it[doneTime] = action.doneTime.toJavaLocalDateTime()
            it[repair] = action.repair
            it[operationType] = action.operationType
            it[employeeId] = action.employeeId.toInt()
            it[productId] = action.productId
        }
        action.copy(id = id.value)
    }

    suspend fun batchCreate(actions: List<Action>) = safetySuspendTransactionAsync(database) {
        Actions.batchInsert(actions) { action: Action ->
            this[Actions.doneTime] = action.doneTime.toJavaLocalDateTime()
            this[Actions.repair] = action.repair
            this[Actions.operationType] = action.operationType
            this[Actions.employeeId] = action.employeeId.toInt()
            this[Actions.productId] = action.productId
        }
            .map(::extractAction)
    }

    suspend fun deleteById(id: Long) = safetySuspendTransactionAsync(database) {
        Actions.deleteWhere { Actions.id eq id }
    }

    private fun extractAction(row: ResultRow): Action = Action(
        row[Actions.id].value,
        row[Actions.doneTime].toKotlinLocalDateTime(),
        row[Actions.repair],
        row[Actions.operationType].value,
        row[Actions.employeeId].value.toLong(),
        row[Actions.productId].value
    )
}