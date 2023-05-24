package com.remcoil.model.database

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object OperationTypes : LongIdTable("operation_types") {
    val type = varchar("type", 64)
    val sequenceNumber = integer("sequence_number")
    val specificationId = reference("specification_id", Specifications, onDelete = ReferenceOption.CASCADE)
}