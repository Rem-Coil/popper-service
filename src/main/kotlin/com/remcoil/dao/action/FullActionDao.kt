package com.remcoil.dao.action

import com.remcoil.data.database.FullActions
import com.remcoil.data.model.action.full.FullAction
import com.remcoil.utils.safetySuspendTransactionAsync
import kotlinx.datetime.toKotlinLocalDateTime
import org.jetbrains.exposed.sql.*

class FullActionDao(private val database: Database) {

    suspend fun getAll(): List<FullAction> = safetySuspendTransactionAsync(database) {
        FullActions
            .selectAll()
            .map(::extractFullAction)
    }

    suspend fun getByTaskId(taskId: Int): List<FullAction> = safetySuspendTransactionAsync(database) {
        FullActions
            .select { FullActions.taskId eq taskId}
            .map(::extractFullAction)
    }

    suspend fun getByBatchId(batchId: Long): List<FullAction> = safetySuspendTransactionAsync(database) {
        FullActions
            .select { FullActions.batchId eq batchId and FullActions.active}
            .map(::extractFullAction)
    }

    suspend fun getByBobbinId(bobbinId: Long): List<FullAction> = safetySuspendTransactionAsync(database) {
        FullActions
            .select { FullActions.bobbinId eq bobbinId}
            .map(::extractFullAction)
    }

    suspend fun getById(id: Long): FullAction? = safetySuspendTransactionAsync(database) {
        FullActions
            .select {FullActions.actionId eq id}
            .map(::extractFullAction)
            .firstOrNull()
    }

    private fun extractFullAction(row: ResultRow): FullAction = FullAction(
        row[FullActions.taskId],
        row[FullActions.taskName],
        row[FullActions.taskNumber],
        row[FullActions.batchId],
        row[FullActions.batchNumber],
        row[FullActions.bobbinId],
        row[FullActions.bobbinNumber],
        row[FullActions.actionId],
        row[FullActions.actionType],
        row[FullActions.doneTime].toKotlinLocalDateTime(),
        row[FullActions.successful],
        row[FullActions.operatorId],
        row[FullActions.firstName],
        row[FullActions.secondName],
        row[FullActions.surname]
    )
}