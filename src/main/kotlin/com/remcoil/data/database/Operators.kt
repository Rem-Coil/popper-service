package com.remcoil.data.database

import org.jetbrains.exposed.dao.id.LongIdTable

object Operators: LongIdTable("operators") {
    val firstName = varchar("first_name", 32)
    val secondName = varchar("second_name", 32)
    val surname = varchar("surname", 32)
    val phone = varchar("phone", 16)
    val password = varchar("password", 32)
    val active = bool("active")
    val role = varchar("role", 32)
}