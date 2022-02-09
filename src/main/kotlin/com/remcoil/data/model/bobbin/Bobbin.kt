package com.remcoil.data.model.bobbin

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Bobbin(
    val id: Int,
    @SerialName("task_id")
    val taskId: Int,
    @SerialName("bobbin_number")
    val bobbinNumber: String
)