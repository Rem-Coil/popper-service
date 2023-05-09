package com.remcoil.dao.v2

import com.remcoil.data.database.v2.Kits
import com.remcoil.data.model.v2.Kit
import com.remcoil.utils.safetySuspendTransactionAsync
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class KitDao(private val database: Database) {
    suspend fun getById(id: Long): Kit? = safetySuspendTransactionAsync(database) {
        Kits
            .select { Kits.id eq id }
            .map(::extractKit)
            .firstOrNull()
    }

    suspend fun getBySpecificationId(id: Long): List<Kit> = safetySuspendTransactionAsync(database) {
        Kits
            .select { Kits.specificationId eq id }
            .map(::extractKit)
    }

    suspend fun getAll(): List<Kit> = safetySuspendTransactionAsync(database) {
        Kits
            .selectAll()
            .map(::extractKit)
    }

    suspend fun create(kit: Kit): Kit = safetySuspendTransactionAsync(database) {
        val id = Kits.insertAndGetId {
            it[kitNumber] = kit.kitNumber
            it[batchesQuantity] = kit.batchesQuantity
            it[batchSize] = kit.batchSize
            it[specificationId] = kit.specificationId
        }
        kit.copy(id = id.value)
    }

    suspend fun update(kit: Kit) = safetySuspendTransactionAsync(database) {
        Kits.update({ Kits.id eq kit.id }) {
            it[kitNumber] = kit.kitNumber
            it[batchesQuantity] = kit.batchesQuantity
            it[batchSize] = kit.batchSize
            it[specificationId] = kit.specificationId
        }
    }

    suspend fun deleteById(id: Long) = safetySuspendTransactionAsync(database) {
        Kits.deleteWhere { Kits.id eq id }
    }

    private fun extractKit(row: ResultRow): Kit = Kit(
        row[Kits.id].value,
        row[Kits.kitNumber],
        row[Kits.batchesQuantity],
        row[Kits.batchSize],
        row[Kits.specificationId].value
    )
}