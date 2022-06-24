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
    var quantity: Int,
    var winding: Int,
    var output: Int,
    var isolation: Int,
    var molding: Int,
    var crimping: Int,
    var quality: Int,
    var testing: Int
)
