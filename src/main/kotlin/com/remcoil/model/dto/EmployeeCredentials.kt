package com.remcoil.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class EmployeeCredentials(
    val phone: String,
    val password: String,
)
