package com.remcoil.data.model.v2

import kotlinx.serialization.Serializable

@Serializable
data class EmployeeCredentials(
    val phone: String,
    val password: String,
)
