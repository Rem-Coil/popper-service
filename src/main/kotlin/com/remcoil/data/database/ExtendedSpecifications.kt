package com.remcoil.data.database

import org.jetbrains.exposed.sql.Table

object ExtendedSpecifications : Table("extended_specifications") {
    val id = long("id")
    val specificationTitle = varchar("specification_title", 32)
    val productType = varchar("product_type", 32)
    val testedPercentage = integer("tested_percentage")
    val kitQuantity = integer("kit_quantity")
}