package com.remcoil.data.database

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.`java-time`.datetime

object Actions: IntIdTable("action") {
    val operatorId = reference("operator_id", Operators)
    val bobbinId = reference("bobbin_id", Bobbins)
    val actionType = varchar("action_type",50)
    val doneTime = datetime("done_time")
}