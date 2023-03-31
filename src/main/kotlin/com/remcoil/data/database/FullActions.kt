package com.remcoil.data.database

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object FullActions : Table("full_actions") {
    val specificationId = long("specification_id")
    val specificationTitle = varchar("specification_title", 32)
    val productType = varchar("product_type", 32)

    val kitId = long("kit_id")
    val kitNumber = varchar("kit_number", 64)

    val batchId = long("batch_id")
    val batchNumber = varchar("batch_number", 64)

    val productId = long("product_id")
    val productNumber = varchar("product_number", 128)
    val active = bool("active")

    val actionId = long("action_id")
    val doneTime = datetime("done_time")
    val successful = bool("successful")

    val specification_action_id = long("specification_action_id")
    val actionType = varchar("action_type", 64)
    val sequenceNumber = integer("sequence_number")

    val comment = text("comment")

    val employeeId = integer("employee_id")
    val firstName = varchar("first_name", 32)
    val secondName = varchar("second_name", 32)
    val surname = varchar("surname", 32)
    val role = varchar("role", 32)
}