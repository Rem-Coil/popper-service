package com.remcoil.data.database

import org.jetbrains.exposed.dao.id.IntIdTable

object Tasks: IntIdTable("task") {
    val taskName = varchar("task_name", 32)
    val taskNumber = varchar("task_number", 32)
}