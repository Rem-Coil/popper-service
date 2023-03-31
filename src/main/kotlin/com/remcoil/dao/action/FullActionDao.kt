package com.remcoil.dao.action

import com.remcoil.data.database.FullActions
import com.remcoil.data.model.action.full.FlatFullAction
import com.remcoil.utils.safetySuspendTransactionAsync
import kotlinx.datetime.toKotlinLocalDateTime
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll

class FullActionDao(private val database: Database) {

    suspend fun getAll(): List<FlatFullAction> = safetySuspendTransactionAsync(database) {
        FullActions
            .selectAll()
            .map(::extractFlatFullAction)
    }
    suspend fun getById(id: Long): FlatFullAction? = safetySuspendTransactionAsync(database) {
        FullActions
            .select { FullActions.actionId eq id }
            .map(::extractFlatFullAction)
            .firstOrNull()
    }

    private fun extractFlatFullAction(row: ResultRow): FlatFullAction = FlatFullAction(
        row[FullActions.specificationId],
        row[FullActions.specificationTitle],
        row[FullActions.productType],
        row[FullActions.kitId],
        row[FullActions.kitNumber],
        row[FullActions.batchId],
        row[FullActions.batchNumber],
        row[FullActions.productId],
        row[FullActions.productNumber],
        row[FullActions.active],
        row[FullActions.actionId],
        row[FullActions.doneTime].toKotlinLocalDateTime(),
        row[FullActions.successful],
        row[FullActions.specification_action_id],
        row[FullActions.actionType],
        row[FullActions.sequenceNumber],
        row[FullActions.comment],
        row[FullActions.employeeId],
        row[FullActions.firstName],
        row[FullActions.secondName],
        row[FullActions.surname],
        row[FullActions.role]
    )
}