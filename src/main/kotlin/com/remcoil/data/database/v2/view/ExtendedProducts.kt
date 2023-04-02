package com.remcoil.data.database.v2.view

import org.jetbrains.exposed.sql.Table

object ExtendedProducts : Table("extended_products") {
    val id = long("id")
    val batchId = long("batch_id")
    val productNumber = varchar("product_number", 128)
    val active = bool("active")
}