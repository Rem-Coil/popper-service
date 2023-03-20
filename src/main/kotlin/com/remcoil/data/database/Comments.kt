package com.remcoil.data.database

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table


object Comments: Table("comments") {
    val actionId = reference("action_id", Actions, onDelete = ReferenceOption.CASCADE)
    val comment = text("comment")

    override val primaryKey = PrimaryKey(actionId)
}