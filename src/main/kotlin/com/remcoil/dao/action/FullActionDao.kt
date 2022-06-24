package com.remcoil.dao.action

import com.remcoil.data.database.FullActions
import com.remcoil.data.model.action.FullAction
import kotlinx.datetime.toKotlinLocalDateTime
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class FullActionDao(private val database: Database) {

    fun getAll(): List<FullAction> = transaction(database) {
        FullActions
            .selectAll()
            .map(::extractFullAction)
    }

    fun getByTaskId(taskId: Int): List<FullAction> = transaction(database) {
        FullActions
            .select { FullActions.taskId eq taskId}
            .map(::extractFullAction)
    }

    fun getByBatchId(batchId: Long): List<FullAction> = transaction(database) {
        FullActions
            .select { FullActions.batchId eq batchId }
            .map(::extractFullAction)
    }

    fun getByBobbinId(bobbinId: Long): List<FullAction> = transaction(database) {
        FullActions
            .select { FullActions.bobbinId eq bobbinId}
            .map(::extractFullAction)
    }

    fun getById(id: Long): FullAction? = transaction(database) {
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