package com.remcoil.data.model.v2

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Employee(
    val id: Long,
    @SerialName("first_name")
    val firstName: String,
    @SerialName("last_name")
    val lastName: String,
    val phone: String,
    val password: String,
    val active: Boolean,
    val role: EmployeeRole
)
