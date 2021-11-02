package com.remcoil.data.database

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object Bobbins: IntIdTable("bobbin") {
    val taskId = reference("task_id", Tasks, onDelete = ReferenceOption.CASCADE)
    val bobbinNumber = varchar("bobbin_number", 32)
}