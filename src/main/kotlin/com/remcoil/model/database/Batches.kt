package com.remcoil.model.database

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object Batches: LongIdTable("batches") {
    val batchNumber = integer("batch_number")
    val kitId = reference("kit_id", Kits, onDelete = ReferenceOption.CASCADE)
}