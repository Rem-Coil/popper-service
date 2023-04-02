package com.remcoil.data.database.v2.view

import org.jetbrains.exposed.sql.Table

object ExtendedKits : Table("extended_kits"){
    val id = long("id")
    val kitNumber = varchar("kit_number", 64)
    val batchesQuantity = integer("batches_quantity")
    val batchSize = integer("batch_size")
    val specificationId = long("specification_id")
}