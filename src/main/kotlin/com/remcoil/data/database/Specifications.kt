package com.remcoil.data.database

import org.jetbrains.exposed.dao.id.LongIdTable

object Specifications: LongIdTable("specifications") {
    val specificationTitle = varchar("specification_title", 32)
    val productType = varchar("product_type", 32)
    val testedPercentage = integer("tested_percentage")
}