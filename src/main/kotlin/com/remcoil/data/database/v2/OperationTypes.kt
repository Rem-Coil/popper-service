package com.remcoil.data.database.v2

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object OperationTypes : LongIdTable("operation_types") {
    val type = varchar("type", 64)
    val sequenceNumber = integer("sequence_number")
    val specificationId = reference("specification_id", Specifications, onDelete = ReferenceOption.CASCADE)
}