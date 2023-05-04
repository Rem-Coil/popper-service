package com.remcoil.dao.v2

import com.remcoil.data.database.v2.ControlActions
import com.remcoil.data.model.v2.ControlAction
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

    suspend fun create(controlAction: ControlAction): ControlAction = safetySuspendTransactionAsync(database) {
        val id = ControlActions.insertAndGetId {
            it[doneTime] = controlAction.doneTime.toJavaLocalDateTime()
            it[successful] = controlAction.successful
            it[controlType] = controlAction.controlType
            it[comment] = controlAction.comment
            it[operationType] = controlAction.operationType
            it[employeeId] = controlAction.employeeId.toInt()
            it[productId] = controlAction.productId
        }
        controlAction.copy(id = id.value)
    }

    suspend fun update(controlAction: ControlAction) = safetySuspendTransactionAsync(database) {
        ControlActions.update({ ControlActions.id eq controlAction.id }) {
            it[doneTime] = controlAction.doneTime.toJavaLocalDateTime()
            it[successful] = controlAction.successful
            it[controlType] = controlAction.controlType
            it[comment] = controlAction.comment
            it[operationType] = controlAction.operationType
            it[employeeId] = controlAction.employeeId.toInt()
            it[productId] = controlAction.productId
        }
    }

    suspend fun deleteById(id: Long) = safetySuspendTransactionAsync(database) {
        ControlActions.deleteWhere { ControlActions.id eq id }
    }

    private fun extractControlAction(row: ResultRow): ControlAction = ControlAction(
        id = row[ControlActions.id].value,
        doneTime = row[ControlActions.doneTime].toKotlinLocalDateTime(),
        successful = row[ControlActions.successful],
        controlType = row[ControlActions.controlType],
        comment = row[ControlActions.comment],
        operationType = row[ControlActions.operationType].value,
        employeeId = row[ControlActions.employeeId].value.toLong(),
        productId = row[ControlActions.productId].value
    )
}