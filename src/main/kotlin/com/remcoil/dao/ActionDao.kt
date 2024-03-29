package com.remcoil.dao

import com.remcoil.model.database.Actions
import com.remcoil.model.database.view.ExtendedActions
import com.remcoil.model.dto.Action
import com.remcoil.utils.safetySuspendTransactionAsync
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList

class ActionDao(private val database: Database){

    suspend fun getAll(): List<Action> = safetySuspendTransactionAsync(database) {
        Actions
            .selectAll()
            .map(::extractAction)
    }

    suspend fun getBySpecificationId(id: Long): List<com.remcoil.model.dto.ExtendedAction> = safetySuspendTransactionAsync(database) {
        ExtendedActions
            .select { ExtendedActions.specificationId eq id }
            .map(::extractExtendedAction)
    }

    suspend fun getByKitId(id: Long): List<com.remcoil.model.dto.ExtendedAction> = safetySuspendTransactionAsync(database) {
        ExtendedActions
            .select { ExtendedActions.kitId eq id }
            .map(::extractExtendedAction)
    }

    suspend fun getByProductId(id: Long): List<Action> = safetySuspendTransactionAsync(database) {
        Actions
            .select { Actions.productId eq id }
            .map(::extractAction)
    }

    suspend fun getById(id: Long) = safetySuspendTransactionAsync(database) {
        Actions
            .select { Actions.id eq id }
            .map(::extractAction)
            .firstOrNull()
    }

    suspend fun update(action: Action) = safetySuspendTransactionAsync(database) {
        Actions.update({ Actions.id eq action.id }) {
            it[doneTime] = action.doneTime.toJavaLocalDateTime()
            it[repair] = action.repair
            it[operationType] = action.operationType
            it[employeeId] = action.employeeId
            it[productId] = action.productId
        }
    }

    suspend fun create(action: Action): Action = safetySuspendTransactionAsync(database) {
        val id = Actions.insertAndGetId {
            it[doneTime] = action.doneTime.toJavaLocalDateTime()
            it[repair] = action.repair
            it[operationType] = action.operationType
            it[employeeId] = action.employeeId
            it[productId] = action.productId
        }
        action.copy(id = id.value)
    }

    suspend fun batchCreate(actions: List<Action>): List<Action> = safetySuspendTransactionAsync(database) {
        Actions.batchInsert(actions) { action: Action ->
            this[Actions.doneTime] = action.doneTime.toJavaLocalDateTime()
            this[Actions.repair] = action.repair
            this[Actions.operationType] = action.operationType
            this[Actions.employeeId] = action.employeeId
            this[Actions.productId] = action.productId
        }
            .map(::extractAction)
    }

    suspend fun deleteById(id: Long) = safetySuspendTransactionAsync(database) {
        Actions.deleteWhere { Actions.id eq id }
    }

    suspend fun deleteByProducts(idList: List<Long>) = safetySuspendTransactionAsync(database) {
        Actions.deleteWhere { productId.inList(idList)}
    }

    private fun extractAction(row: ResultRow): Action = Action(
        row[Actions.id].value,
        row[Actions.doneTime].toKotlinLocalDateTime(),
        row[Actions.repair],
        row[Actions.operationType].value,
        row[Actions.employeeId]?.value ?: 0,
        row[Actions.productId].value
    )

    private fun extractExtendedAction(row: ResultRow): com.remcoil.model.dto.ExtendedAction =
        com.remcoil.model.dto.ExtendedAction(
            row[ExtendedActions.id],
            row[ExtendedActions.doneTime].toKotlinLocalDateTime(),
            row[ExtendedActions.repair],
            row[ExtendedActions.operationType],
            row[ExtendedActions.employeeId] ?: 0,
            row[ExtendedActions.productId],
            row[ExtendedActions.active],
            row[ExtendedActions.batchId],
            row[ExtendedActions.kitId],
            row[ExtendedActions.specificationId]
        )
}