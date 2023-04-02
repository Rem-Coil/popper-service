package com.remcoil.data.database.v2.view

import org.jetbrains.exposed.sql.Table

object ExtendedBatches : Table("extended_batches") {
    val id = long("id")
    val batchNumber = varchar("batch_number", 96)
    val kitId = long("kit_id")
}