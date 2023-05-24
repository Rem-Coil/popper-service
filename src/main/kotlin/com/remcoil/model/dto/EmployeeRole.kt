package com.remcoil.model.dto

enum class EmployeeRole {
    OPERATOR,
    QUALITY_ENGINEER,
    ADMIN;

    fun isMatch(role: String): Boolean = this.name == role
}