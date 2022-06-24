package com.remcoil.data.model.batch

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class Batch(
    val id: Long,
    @SerialName("task_id")
    val taskId: Int,
    @SerialName("batch_number")
    val batchNumber: String
)