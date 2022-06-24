package com.remcoil.data.database

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object Bobbins: IntIdTable("bobbin") {
    val batchId = reference("batch_id", Batches, onDelete = ReferenceOption.CASCADE)
    val bobbinNumber = varchar("bobbin_number", 128)
}