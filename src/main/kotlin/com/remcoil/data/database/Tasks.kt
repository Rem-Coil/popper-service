package com.remcoil.data.database

import org.jetbrains.exposed.dao.id.IntIdTable

object Tasks: IntIdTable("task") {
    val task_name = varchar("task_name", 32)
    val task_number = varchar("task_number", 32)
    val quantity = integer("quantity")
    val winding = integer("winding")
    val output = integer("output")
    val isolation = integer("isolation")
    val molding = integer("molding")
    val crimping = integer("crimping")
    val quality = integer("quality")
    val testing = integer("testing")
}