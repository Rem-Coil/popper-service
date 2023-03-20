package com.remcoil.data.database

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object SpecificationActions : LongIdTable("specification_actions") {
    val actionType = varchar("action_type", 64)
    val sequenceNumber = integer("sequence_number")
    val specificationId = reference("specification_id", Specifications, onDelete = ReferenceOption.CASCADE)
}