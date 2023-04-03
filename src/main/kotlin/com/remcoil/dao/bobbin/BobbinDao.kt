package com.remcoil.dao.bobbin

import com.remcoil.data.database.Bobbins
import com.remcoil.data.model.bobbin.Bobbin
import com.remcoil.utils.safetySuspendTransactionAsync
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class BobbinDao(private val database: Database) {

    suspend fun getAll(): List<Bobbin> = safetySuspendTransactionAsync(database) {
        Bobbins
            .selectAll()
            .map(::extractBobbin)
    }

    suspend fun getByBatchId(id: Long): List<Bobbin> = safetySuspendTransactionAsync(database) {
        Bobbins
            .select { Bobbins.batchId eq id }
            .map(::extractBobbin)
    }

    suspend fun getByBatchesId(idList: List<Long>): List<Bobbin> = safetySuspendTransactionAsync(database) {
        Bobbins
            .select { Bobbins.batchId inList(idList) }
            .map(::extractBobbin)
    }

    suspend fun getById(id: Long): Bobbin? = safetySuspendTransactionAsync(database) {
        Bobbins
            .select { Bobbins.id eq id }
            .map(::extractBobbin)
            .firstOrNull()
    }

    suspend fun createBobbin(bobbin: Bobbin): Bobbin = safetySuspendTransactionAsync(database) {
        val id = Bobbins.insertAndGetId {
            it[batchId] = bobbin.batchId
            it[bobbinNumber] = bobbin.bobbinNumber
            it[active] = bobbin.active
        }
        bobbin.copy(id = id.value)
    }

    suspend fun deleteById(id: Long) = safetySuspendTransactionAsync(database){
        Bobbins.deleteWhere { Bobbins.id eq id }
    }

    suspend fun deactivateById(id: Long, bobbinNumber: String) = safetySuspendTransactionAsync(database) {
        Bobbins
            .update({Bobbins.id eq id}) {
                it[active] = false
                it[Bobbins.bobbinNumber] = "$bobbinNumber def"
            }
    }

    suspend fun updateBobbin(bobbin: Bobbin) = safetySuspendTransactionAsync(database) {
        Bobbins.update({Bobbins.id eq bobbin.id}) {
            it[batchId] = bobbin.batchId
            it[bobbinNumber] = bobbin.bobbinNumber
            it[active] = bobbin.active
        }
    }

    private fun extractBobbin(row: ResultRow): Bobbin = Bobbin(
        row[Bobbins.id].value,
        row[Bobbins.batchId].value,
        row[Bobbins.bobbinNumber],
        row[Bobbins.active]
    )
}