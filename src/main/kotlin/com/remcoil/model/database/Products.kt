package com.remcoil.model.database

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object Products: LongIdTable("products") {
    val batchId = reference("batch_id", Batches, onDelete = ReferenceOption.CASCADE)
    val productNumber = integer("product_number")
    val active = bool("active")
}