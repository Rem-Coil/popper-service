package com.remcoil.data.database

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object Batches: LongIdTable("batch") {
    val taskId = reference("task_id", Tasks, onDelete = ReferenceOption.CASCADE)
    val batchNumber = varchar("batch_number", 64)
}