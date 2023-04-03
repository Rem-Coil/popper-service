package com.remcoil.dao.action

import com.remcoil.data.database.FullActions
import com.remcoil.data.model.action.full.FlatFullAction
import com.remcoil.utils.safetySuspendTransactionAsync
import kotlinx.datetime.toKotlinLocalDateTime
import org.jetbrains.exposed.sql.*

class FullActionDao(private val database: Database) {

    suspend fun getAll(): List<FlatFullAction> = safetySuspendTransactionAsync(database) {
        FullActions
            .selectAll()
            .map(::extractFlatFullAction)
    }

    suspend fun getByTaskId(taskId: Int): List<FlatFullAction> = safetySuspendTransactionAsync(database) {
        FullActions
            .select { FullActions.taskId eq taskId}
            .map(::extractFlatFullAction)
    }

    suspend fun getByBatchId(batchId: Long): List<FlatFullAction> = safetySuspendTransactionAsync(database) {
        FullActions
            .select { FullActions.batchId eq batchId}
            .map(::extractFlatFullAction)
    }

    suspend fun getByBobbinId(bobbinId: Long): List<FlatFullAction> = safetySuspendTransactionAsync(database) {
        FullActions
            .select { FullActions.bobbinId eq bobbinId}
            .map(::extractFlatFullAction)
    }

    suspend fun getById(id: Long): FlatFullAction? = safetySuspendTransactionAsync(database) {
        FullActions
            .select {FullActions.actionId eq id}
            .map(::extractFlatFullAction)
            .firstOrNull()
    }

    private fun extractFlatFullAction(row: ResultRow): FlatFullAction = FlatFullAction(
        row[FullActions.taskId],
        row[FullActions.taskName],
        row[FullActions.taskNumber],
        row[FullActions.batchId],
        row[FullActions.batchNumber],
        row[FullActions.bobbinId],
        row[FullActions.bobbinNumber],
        row[FullActions.active],
        row[FullActions.actionId],
        row[FullActions.actionType],
        row[FullActions.doneTime].toKotlinLocalDateTime(),
        row[FullActions.successful],
        row[FullActions.comment],
        row[FullActions.operatorId],
        row[FullActions.firstName],
        row[FullActions.secondName],
        row[FullActions.surname],
        row[FullActions.role]
    )
}