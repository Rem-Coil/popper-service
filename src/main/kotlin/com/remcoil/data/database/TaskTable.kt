package com.remcoil.data.database

import org.jetbrains.exposed.dao.id.IntIdTable

object TaskTable: IntIdTable("task") {
    val taskName = varchar("task_name", 32)
    val taskNumber = varchar("task_number", 32)
    val quantity = integer("quantity")
}