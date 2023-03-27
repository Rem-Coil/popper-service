package com.remcoil.data.database

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.datetime

object Actions: LongIdTable("action") {
    val operatorId = reference("operator_id", Operators)
    val bobbinId = reference("bobbin_id", Bobbins)
    val actionType = varchar("action_type",50)
    val doneTime = datetime("done_time")
    val successful = bool("successful")
}