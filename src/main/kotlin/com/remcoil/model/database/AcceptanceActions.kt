package com.remcoil.model.database

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.datetime

object AcceptanceActions : LongIdTable("acceptance_actions") {
    val doneTime = datetime("done_time")
    val productId = reference("product_id", Products, onDelete = ReferenceOption.CASCADE)
    val employeeId = reference("employee_id", Employees, onDelete = ReferenceOption.SET_NULL).nullable()
}