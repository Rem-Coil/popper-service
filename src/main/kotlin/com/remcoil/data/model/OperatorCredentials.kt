package com.remcoil.data.model

import kotlinx.serialization.Serializable

@Serializable
class OperatorCredentials(
    val id: Int?,
    val firstname: String,
    val second_name: String,
    val surname: String,
    val phone: String,
    val password: String,
)