package com.remcoil.data.model.operator

import kotlinx.serialization.Serializable

@Serializable
class OperatorCredentials(
    val phone: String,
    val password: String,
)