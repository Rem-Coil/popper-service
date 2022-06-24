package com.remcoil.data.model.batch

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class FullBatch(
    val id: Long,
    @SerialName("task_id")
    val task_id: Int,
    @SerialName("batch_number")
    val batchNumber: String,
    val quantity: Int = 0,
    var winding: Int = 0,
    var output: Int = 0,
    var isolation: Int = 0,
    var molding: Int = 0,
    var crimping: Int = 0,
    var quality: Int = 0,
    var testing: Int = 0
)
