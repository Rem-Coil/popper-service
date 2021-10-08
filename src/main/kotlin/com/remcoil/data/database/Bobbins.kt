package com.remcoil.data.database

import org.jetbrains.exposed.dao.id.IntIdTable

object Bobbins: IntIdTable("bobbin") {
    val taskId = reference("task_id", Tasks)
    val bobbinNumber = varchar("bobbin_number", 32)
}