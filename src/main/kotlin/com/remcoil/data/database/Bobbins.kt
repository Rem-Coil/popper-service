package com.remcoil.data.database

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object Bobbins: LongIdTable("bobbin") {
    val batchId = reference("batch_id", Batches, onDelete = ReferenceOption.CASCADE)
    val bobbinNumber = varchar("bobbin_number", 128)
}