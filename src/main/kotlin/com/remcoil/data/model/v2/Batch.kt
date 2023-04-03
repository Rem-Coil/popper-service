package com.remcoil.data.model.v2

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Batch(
    var id: Long = 0,
    @SerialName("batch_number")
    var batchNumber: String,
    @SerialName("kit_id")
    val kitId: Long
)
