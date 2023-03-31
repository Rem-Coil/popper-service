package com.remcoil.data.model.employee

import kotlinx.serialization.Serializable

@Serializable
class EmployeeCredentials(
    val phone: String,
    val password: String,
)