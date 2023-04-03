package com.remcoil.data.model.batch

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
class BatchIdentity(
    @SerialName("task_id")
    val taskId: Int
)