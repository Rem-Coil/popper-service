package com.remcoil.data.model.operator

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Operator(
    val id: Int,
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
