package com.remcoil.data.model.batch

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Batch(
    val id: Long,
    @SerialName("batch_number")
    var batchNumber: String,
    @SerialName("kit_id")
    val kitId: Long
)