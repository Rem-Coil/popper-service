package com.remcoil.data.model.task

import kotlinx.serialization.Serializable

@Serializable
data class TaskIdentity(
    val task_name: String,
    val task_number: String,
    val quantity: Int
)
