package com.remcoil.dao.bobbin

import com.remcoil.data.database.Bobbins
import com.remcoil.data.model.bobbin.Bobbin
import com.remcoil.utils.safetySuspendTransactionAsync
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class BobbinDao(private val database: Database) {

    fun getAll(): List<Bobbin> = transaction(database) {
        Bobbins
            .selectAll()
            .map(::extractBobbin)
    }

    fun getByBatchId(id: Long): List<Bobbin> = transaction(database) {
        Bobbins
            .select { Bobbins.batchId eq id }
            .map(::extractBobbin)
    }

    fun getByBatchesId(idList: List<Long>): List<Bobbin> = transaction(database) {
        Bobbins
            .select { Bobbins.batchId inList(idList) }
            .map(::extractBobbin)
    }

    fun getById(id: Long): Bobbin? = transaction(database) {
        Bobbins
            .select { Bobbins.id eq id }
            .map(::extractBobbin)
            .firstOrNull()
    }

    suspend fun create(bobbin: Bobbin): Bobbin = safetySuspendTransactionAsync(database) {
        val id = Bobbins.insertAndGetId {
            it[batchId] = bobbin.batchId
            it[bobbinNumber] = bobbin.bobbinNumber
        }
        bobbin.copy(id = id.value)
    }

    suspend fun deleteById(id: Long) = safetySuspendTransactionAsync(database){
        Bobbins.deleteWhere { Bobbins.id eq id }
    }

    private fun extractBobbin(row: ResultRow): Bobbin = Bobbin(
        row[Bobbins.id].value,
        row[Bobbins.batchId].value,
        row[Bobbins.bobbinNumber]
    )
}