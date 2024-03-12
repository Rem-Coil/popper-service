package com.remcoil.model.database

import org.jetbrains.exposed.dao.id.LongIdTable

object Employees: LongIdTable("employees") {
    val firstName = varchar("first_name", 32)
    val lastName = varchar("last_name", 32)
    val phone = varchar("phone", 16)
    val password = varchar("password", 32)
    val active = bool("active")
    val role = varchar("role", 32)
}