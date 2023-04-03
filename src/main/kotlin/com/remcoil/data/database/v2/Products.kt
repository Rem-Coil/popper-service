package com.remcoil.data.database.v2

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object Products: LongIdTable("products") {
    val batchId = reference("batch_id", Batches, onDelete = ReferenceOption.CASCADE)
    val productNumber = integer("product_number")
    val active = bool("active")
}