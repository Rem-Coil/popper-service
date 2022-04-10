package com.remcoil.data.model.task

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Task(
    val id: Int,
    @SerialName("task_name")
    val taskName: String,
    @SerialName("task_number")
    val taskNumber: String,
    val quantity: Int
) {
    constructor(identity: TaskIdentity) : this(
        1,
        identity.taskName,
        identity.taskNumber,
        identity.quantity,
    )
}
