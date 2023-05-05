package com.remcoil.data.database.v2.view

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object ExtendedControlActions : Table("extended_control_actions") {
    val id = long("id")
    val doneTime = datetime("done_time")
    val successful = bool("successful")
    val controlType = varchar("control_type", 64)
    val comment = text("comment")
    val operationType = long("operation_type")
    val employeeId = long("employee_id")
    val productId = long("product_id")
    val batchId = long("batch_id")
    val kitId = long("kit_id")
}