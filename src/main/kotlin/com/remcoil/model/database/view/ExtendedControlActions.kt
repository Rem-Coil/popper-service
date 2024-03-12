package com.remcoil.model.database.view

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object ExtendedControlActions : Table("extended_control_actions") {
    val id = long("id")
    val doneTime = datetime("done_time")
    val successful = bool("successful")
    val controlType = varchar("control_type", 64)
    val comment = text("comment").nullable()
    val operationType = long("operation_type")
    val employeeId = long("employee_id").nullable()
    val productId = long("product_id")
    val needRepair = bool("need_repair")
    val active = bool("active")
    val batchId = long("batch_id")
    val kitId = long("kit_id")
    val specificationId = long("specification_id")
}