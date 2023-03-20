package com.remcoil.data.database

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object Products: LongIdTable("products") {
    val batchId = reference("batch_id", Batches, onDelete = ReferenceOption.CASCADE)
    val productNumber = varchar("product_number", 128)
    val active = bool("active")
}