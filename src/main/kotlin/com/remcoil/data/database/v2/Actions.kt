package com.remcoil.data.database.v2

import com.remcoil.data.database.Operators
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.datetime

object Actions : LongIdTable("actions") {
    val doneTime = datetime("done_time")
    val repair = bool("repair")
    val operationType = reference("operation_type", OperationTypes, onDelete = ReferenceOption.CASCADE)
    val employeeId = reference("employee_id", Operators, onDelete = ReferenceOption.SET_NULL)
    val productId = reference("product_id", Products, onDelete = ReferenceOption.CASCADE)
}