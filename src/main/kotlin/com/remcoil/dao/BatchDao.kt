package com.remcoil.dao

import com.remcoil.model.database.Batches
import com.remcoil.model.dto.Batch
import com.remcoil.utils.safetySuspendTransactionAsync
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq


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

    suspend fun getByKitId(id: Long): List<Batch> = safetySuspendTransactionAsync(database) {
        Batches
            .select { Batches.kitId eq id }
            .map(::extractBatch)
    }

    suspend fun create(batch: Batch) = safetySuspendTransactionAsync(database) {
        val id = Batches.insertAndGetId {
            it[kitId] = batch.kitId
            it[batchNumber] = batch.batchNumber
        }
        batch.copy(id = id.value)
    }

    suspend fun batchCreate(batches: List<Batch>) = safetySuspendTransactionAsync(database) {
        Batches.batchInsert(batches) { batch: Batch ->
            this[Batches.kitId] = batch.kitId
            this[Batches.batchNumber] = batch.batchNumber
        }
            .map(::extractBatch)
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
        resultRow[Batches.id].value,
        resultRow[Batches.batchNumber],
        resultRow[Batches.kitId].value
    )
}
