package com.remcoil.dao.v2

import com.remcoil.data.database.v2.ControlActions
import com.remcoil.data.database.v2.view.ExtendedControlActions
import com.remcoil.data.model.v2.ControlAction
import com.remcoil.data.model.v2.ControlType
import com.remcoil.data.model.v2.ExtendedControlAction
import com.remcoil.utils.safetySuspendTransactionAsync
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class ControlActionDao(private val database: Database) {
    suspend fun getAll() = safetySuspendTransactionAsync(database) {
        ControlActions
            .selectAll()
            .map(::extractControlAction)
    }

    suspend fun getBySpecificationId(id: Long): List<ExtendedControlAction> = safetySuspendTransactionAsync(database) {
        ExtendedControlActions
            .select { ExtendedControlActions.specificationId eq id }
            .map(::extractExtendedControlAction)
    }

    suspend fun getByKitId(id: Long): List<ExtendedControlAction> = safetySuspendTransactionAsync(database) {
        ExtendedControlActions
            .select { ExtendedControlActions.kitId eq id }
            .map(::extractExtendedControlAction)
    }

    suspend fun getByProductId(id: Long): List<ControlAction> = safetySuspendTransactionAsync(database) {
        ControlActions
            .select { ControlActions.productId eq id }
            .map(::extractControlAction)
    }

    suspend fun create(controlAction: ControlAction): ControlAction = safetySuspendTransactionAsync(database) {
        val id = ControlActions.insertAndGetId {
            it[doneTime] = controlAction.doneTime.toJavaLocalDateTime()
            it[successful] = controlAction.successful
            it[controlType] = controlAction.controlType.name
            it[comment] = controlAction.comment
            it[operationType] = controlAction.operationType
            it[employeeId] = controlAction.employeeId
            it[productId] = controlAction.productId
        }
        controlAction.copy(id = id.value)
    }

    suspend fun update(controlAction: ControlAction) = safetySuspendTransactionAsync(database) {
        ControlActions.update({ ControlActions.id eq controlAction.id }) {
            it[doneTime] = controlAction.doneTime.toJavaLocalDateTime()
            it[successful] = controlAction.successful
            it[controlType] = controlAction.controlType.name
            it[comment] = controlAction.comment
            it[operationType] = controlAction.operationType
            it[employeeId] = controlAction.employeeId
            it[productId] = controlAction.productId
        }
    }

    suspend fun deleteById(id: Long) = safetySuspendTransactionAsync(database) {
        ControlActions.deleteWhere { ControlActions.id eq id }
    }

    private fun extractControlAction(row: ResultRow): ControlAction = ControlAction(
        row[ControlActions.id].value,
        row[ControlActions.doneTime].toKotlinLocalDateTime(),
        row[ControlActions.successful],
        ControlType.valueOf(row[ControlActions.controlType]),
        row[ControlActions.comment],
        row[ControlActions.operationType].value,
        row[ControlActions.employeeId]?.value ?: 0,
        row[ControlActions.productId].value
    )

    private fun extractExtendedControlAction(row: ResultRow): ExtendedControlAction = ExtendedControlAction(
        row[ExtendedControlActions.id],
        row[ExtendedControlActions.doneTime].toKotlinLocalDateTime(),
        row[ExtendedControlActions.successful],
        ControlType.valueOf(row[ExtendedControlActions.controlType]),
        row[ExtendedControlActions.comment],
        row[ExtendedControlActions.operationType],
        row[ExtendedControlActions.employeeId] ?: 0,
        row[ExtendedControlActions.productId],
        row[ExtendedControlActions.active],
        row[ExtendedControlActions.batchId],
        row[ExtendedControlActions.kitId],
        row[ExtendedControlActions.specificationId]
    )
}