package com.remcoil.data.model.operator

import kotlinx.serialization.Serializable

@Serializable
data class Operator(
    val id: Int,
    val firstname: String,
    val second_name: String,
    val surname: String,
    val phone: String,
    val password: String
)
