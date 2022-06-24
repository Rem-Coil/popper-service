package com.remcoil.dao.batch

import com.remcoil.data.database.Batches
import com.remcoil.data.model.batch.Batch
import com.remcoil.utils.safetySuspendTransactionAsync
import org.jetbrains.exposed.sql.*


class BatchDao(private val database: Database) {
    suspend fun getAll() = safetySuspendTransactionAsync(database) {
        Batches
            .selectAll()
            .map(::extractBatch)
    }

    suspend fun getById(id: Long): Batch? = safetySuspendTransactionAsync(database) {
        Batches
            .select { Batches.id eq id }
            .map(::extractBatch)
            .firstOrNull()
    }

    suspend fun getByTaskId(id: Int): List<Batch> = safetySuspendTransactionAsync(database) {
        Batches
            .select { Batches.taskId eq id }
            .map(::extractBatch)
    }

    suspend fun createBatch(batch: Batch) = safetySuspendTransactionAsync(database) {
        val id = Batches.insertAndGetId {
            it[taskId] = batch.taskId
            it[batchNumber] = batch.batchNumber
        }
        batch.copy(id = id.value)
    }

    suspend fun deleteBatchById(id: Long) = safetySuspendTransactionAsync(database) {
        Batches.deleteWhere { Batches.id eq id }
    }

    private fun extractBatch(resultRow: ResultRow): Batch = Batch(
        resultRow[Batches.id].value,
        resultRow[Batches.taskId].value,
        resultRow[Batches.batchNumber]
    )
}