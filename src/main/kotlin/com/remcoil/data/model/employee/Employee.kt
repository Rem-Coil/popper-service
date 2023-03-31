package com.remcoil.data.model.employee

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Employee(
    val id: Long,
    @SerialName("first_name")
    val firstName: String,
    @SerialName("second_name")
    val secondName: String,
    val surname: String,
    val phone: String,
    val password: String,
    val active: Boolean,
    val role: String
)
