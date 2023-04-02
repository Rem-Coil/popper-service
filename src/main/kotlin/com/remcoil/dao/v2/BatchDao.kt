package com.remcoil.dao.v2

import com.remcoil.data.database.v2.Batches
import com.remcoil.data.database.v2.view.ExtendedBatches
import com.remcoil.data.model.v2.Batch
import com.remcoil.utils.safetySuspendTransactionAsync
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq


class BatchDao(private val database: Database) {
    suspend fun getAll() = safetySuspendTransactionAsync(database) {
        ExtendedBatches
            .selectAll()
            .map(::extractBatch)
    }

    suspend fun getById(id: Long): Batch? = safetySuspendTransactionAsync(database) {
        ExtendedBatches
            .select { ExtendedBatches.id eq id }
            .map(::extractBatch)
            .firstOrNull()
    }

    suspend fun getByKitId(id: Long): List<Batch> = safetySuspendTransactionAsync(database) {
        ExtendedBatches
            .select { ExtendedBatches.kitId eq id }
            .map(::extractBatch)
    }

    suspend fun create(batch: Batch) = safetySuspendTransactionAsync(database) {
        val id = Batches.insertAndGetId {
            it[kitId] = batch.kitId
            it[batchNumber] = batch.batchNumber
        }
        batch.copy(id = id.value)
    }

    suspend fun deleteById(id: Long) = safetySuspendTransactionAsync(database) {
        Batches.deleteWhere { Batches.id eq id }
    }

    suspend fun update(batch: Batch) = safetySuspendTransactionAsync(database) {
        Batches.update({ Batches.id eq batch.id }) {
            it[kitId] = batch.kitId
            it[batchNumber] = batch.batchNumber
        }
    }

    private fun extractBatch(resultRow: ResultRow): Batch = Batch(
        resultRow[ExtendedBatches.id],
        resultRow[ExtendedBatches.batchNumber],
        resultRow[ExtendedBatches.kitId]
    )
}
