package com.remcoil.data.database.v2

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object Batches: LongIdTable("batches") {
    val batchNumber = integer("batch_number")
    val kitId = reference("kit_id", Kits, onDelete = ReferenceOption.CASCADE)
}