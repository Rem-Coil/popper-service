package com.remcoil.model.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Batch(
    val id: Long = 0,
    @SerialName("batch_number")
    val batchNumber: Int,
    @SerialName("kit_id")
    val kitId: Long
)
