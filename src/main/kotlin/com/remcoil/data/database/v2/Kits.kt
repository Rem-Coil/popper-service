package com.remcoil.data.database.v2

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object Kits : LongIdTable("kits") {
    val kitNumber = varchar("kit_number", 64)
    val batchesQuantity = integer("batches_quantity")
    val batchSize = integer("batch_size")
    val specificationId = reference("specification_id", Specifications, onDelete = ReferenceOption.CASCADE)
}