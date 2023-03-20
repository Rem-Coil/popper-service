package com.remcoil.data.database

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.datetime

object Actions : LongIdTable("actions") {
    val operatorId = reference("operator_id", Operators, onDelete = ReferenceOption.SET_NULL)
    val productId = reference("product_id", Products, onDelete = ReferenceOption.CASCADE)
    val actionTypeId = reference("action_type_id", SpecificationActions, onDelete = ReferenceOption.CASCADE)
    val doneTime = datetime("done_time")
    val successful = bool("successful")
}