package com.remcoil.data.database

import org.jetbrains.exposed.sql.Table


object DefectComments: Table("defect_comment") {
    val actionId = reference("action_id", Actions)
    val comment = text("comment")

    override val primaryKey = PrimaryKey(actionId)
}