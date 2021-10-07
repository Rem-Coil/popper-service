package com.remcoil.data.model.operator

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Operator(
    val id: Int,
    val firstname: String,
    @SerialName("second_name")
    val secondName: String,
    val surname: String,
    val phone: String,
    val password: String
)
