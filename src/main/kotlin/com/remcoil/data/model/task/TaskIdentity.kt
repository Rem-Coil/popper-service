package com.remcoil.data.model.task

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TaskIdentity(
    @SerialName("task_name")
    val taskName: String,
    @SerialName("task_number")
    val taskNumber: String,
    @SerialName("batch_number")
    val batchNumber: Int,
    @SerialName("batch_size")
    val batchSize: Int
)
