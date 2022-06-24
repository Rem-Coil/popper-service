package com.remcoil.data.database

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object FullActions:  Table("full_action") {
    val taskId = integer("task_id")
    val taskName = varchar("task_name", 32)
    val taskNumber = varchar("task_number", 32)
    val batchId = long("batch_id")
    val batchNumber = varchar("batch_number", 64)
    val bobbinId = long("bobbin_id")
    val bobbinNumber = varchar("bobbin_number", 128)
    val actionId = long("action_id")
    val actionType = varchar("action_type", 50)
    val doneTime = datetime("done_time")
    val successful = bool("successful")
    val operatorId = integer("operator_id")
    val firstName = varchar("first_name", 32)
    val secondName = varchar("second_name", 32)
    val surname = varchar("surname", 32)
}