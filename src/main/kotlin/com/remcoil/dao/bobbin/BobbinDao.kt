package com.remcoil.dao.bobbin

import com.remcoil.data.database.Bobbins
import com.remcoil.data.model.bobbin.Bobbin
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class BobbinDao(private val database: Database) {

    fun getAll(): List<Bobbin> = transaction(database) {
        Bobbins
            .selectAll()
            .map(::extractBobbin)
    }

    fun getByTaskId(id: Int): List<Bobbin> = transaction(database) {
        Bobbins
            .select { Bobbins.taskId eq id }
            .map(::extractBobbin)
    }

    private fun extractBobbin(row: ResultRow): Bobbin = Bobbin(
        row[Bobbins.id].value,
        row[Bobbins.taskId].value,
        row[Bobbins.bobbinNumber]
    )
}