package com.remcoil.data.model.task

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FullTask(
    val id: Int,
    @SerialName("task_name")
    val taskName: String,
    @SerialName("task_number")
    val taskNumber: String,
    var quantity: Int = 0,
    var winding: Int = 0,
    var output: Int = 0,
    var isolation: Int = 0,
    var molding: Int = 0,
    var crimping: Int = 0,
    var quality: Int = 0,
    var testing: Int = 0
)
