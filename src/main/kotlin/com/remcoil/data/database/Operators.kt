package com.remcoil.data.database

import org.jetbrains.exposed.dao.id.IntIdTable

object Operators: IntIdTable("operator") {
    val firstname = varchar("firstname", 32)
    val secondName = varchar("second_name", 32)
    val surname = varchar("surname", 32)
    val phone = varchar("phone", 16)
    val password = varchar("password", 32)
}