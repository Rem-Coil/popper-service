package com.remcoil.data.database

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object Batches: LongIdTable("batches") {
    val batchNumber = varchar("batch_number", 96)
    val kitId = reference("kit_id", Kits, onDelete = ReferenceOption.CASCADE)
}