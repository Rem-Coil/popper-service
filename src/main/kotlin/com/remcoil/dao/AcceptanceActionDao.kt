package com.remcoil.dao

import com.remcoil.model.database.AcceptanceActions
import com.remcoil.model.database.view.ExtendedAcceptanceActions
import com.remcoil.model.dto.AcceptanceAction
import com.remcoil.model.dto.ExtendedAcceptanceAction
import com.remcoil.utils.safetySuspendTransactionAsync
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class AcceptanceActionDao(private val database: Database) {
    suspend fun getAll(): List<AcceptanceAction> = safetySuspendTransactionAsync(database) {
        AcceptanceActions
            .selectAll()
            .map(::extractAcceptanceAction)
    }

    suspend fun getBySpecificationId(id: Long): List<ExtendedAcceptanceAction> = safetySuspendTransactionAsync(database) {
        ExtendedAcceptanceActions
            .select { ExtendedAcceptanceActions.specificationId eq id }
            .map(::extractExtendedAcceptanceAction)
    }

    suspend fun getByKitId(id: Long): List<ExtendedAcceptanceAction> = safetySuspendTransactionAsync(database) {
        ExtendedAcceptanceActions
            .select { ExtendedAcceptanceActions.kitId eq id }
            .map(::extractExtendedAcceptanceAction)
    }

    suspend fun getByProductId(id: Long) = safetySuspendTransactionAsync(database) {
        AcceptanceActions
            .select { AcceptanceActions.productId eq id }
            .map(::extractAcceptanceAction)
            .firstOrNull()
    }

    suspend fun getById(id: Long) = safetySuspendTransactionAsync(database) {
        AcceptanceActions
            .select { AcceptanceActions.id eq id }
            .map(::extractAcceptanceAction)
            .firstOrNull()
    }

    suspend fun update(acceptanceAction: AcceptanceAction) = safetySuspendTransactionAsync(database) {
        AcceptanceActions.update({AcceptanceActions.id eq acceptanceAction.id}) {
            it[doneTime] = acceptanceAction.doneTime.toJavaLocalDateTime()
            it[productId] = acceptanceAction.productId
            it[employeeId] = acceptanceAction.employeeId
        }
    }

    suspend fun create(acceptanceAction: AcceptanceAction) = safetySuspendTransactionAsync(database) {
        val id = AcceptanceActions.insertAndGetId {
            it[doneTime] = acceptanceAction.doneTime.toJavaLocalDateTime()
            it[productId] = acceptanceAction.productId
            it[employeeId] = acceptanceAction.employeeId
        }
        acceptanceAction.copy(id = id.value)
    }

    suspend fun batchCreate(actions: List<AcceptanceAction>) = safetySuspendTransactionAsync(database) {
        AcceptanceActions.batchInsert(actions) { acceptanceAction ->
            this[AcceptanceActions.doneTime] = acceptanceAction.doneTime.toJavaLocalDateTime()
            this[AcceptanceActions.productId] = acceptanceAction.productId
            this[AcceptanceActions.employeeId] = acceptanceAction.employeeId
        }
            .map(::extractAcceptanceAction)
    }

    suspend fun deleteById(id: Long) = safetySuspendTransactionAsync(database) {
        AcceptanceActions.deleteWhere { AcceptanceActions.id eq id }
    }

    private fun extractAcceptanceAction(row: ResultRow): AcceptanceAction =
        AcceptanceAction(
            row[AcceptanceActions.id].value,
            row[AcceptanceActions.doneTime].toKotlinLocalDateTime(),
            row[AcceptanceActions.productId].value,
            row[AcceptanceActions.employeeId]?.value ?: 0
        )

    private fun extractExtendedAcceptanceAction(row: ResultRow): ExtendedAcceptanceAction =
        ExtendedAcceptanceAction(
            row[ExtendedAcceptanceActions.id],
            row[ExtendedAcceptanceActions.doneTime].toKotlinLocalDateTime(),
            row[ExtendedAcceptanceActions.employeeId] ?: 0,
            row[ExtendedAcceptanceActions.productId],
            row[ExtendedAcceptanceActions.active],
            row[ExtendedAcceptanceActions.batchId],
            row[ExtendedAcceptanceActions.kitId],
            row[ExtendedAcceptanceActions.specificationId]
        )
}