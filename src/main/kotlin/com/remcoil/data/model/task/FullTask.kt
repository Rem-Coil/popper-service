package com.remcoil.data.model.task

import com.remcoil.data.model.batch.FullBatch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FullTask(
    val id: Int,
    @SerialName("task_name")
    val taskName: String,
    @SerialName("task_number")
    val taskNumber: String,
    val batches: List<FullBatch>
)
