package com.remcoil.model.database.view

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object ExtendedActions : Table("extended_actions") {
    val id = long("id")
    val doneTime = datetime("done_time")
    val repair = bool("repair")
    val operationType = long("operation_type")
    val employeeId = long("employee_id").nullable()
    val productId = long("product_id")
    val active = bool("active")
    val batchId = long("batch_id")
    val kitId = long("kit_id")
    val specificationId = long("specification_id")
}