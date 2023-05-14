package com.remcoil.data.database.v2

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.datetime

object ControlActions : LongIdTable("control_actions") {
    val doneTime = datetime("done_time")
    val successful = bool("successful")
    val controlType = varchar("control_type", 64)
    val comment = text("comment").nullable()
    val operationType = reference("operation_type", OperationTypes, onDelete = ReferenceOption.CASCADE)
    val employeeId = reference("employee_id", Employees, onDelete = ReferenceOption.SET_NULL).nullable()
    val productId = reference("product_id", Products, onDelete = ReferenceOption.CASCADE)
}