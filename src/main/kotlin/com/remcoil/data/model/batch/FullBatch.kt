package com.remcoil.data.model.batch

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class FullBatch(
    val id: Long,
    @SerialName("task_id")
    val task_id: Int,
    @SerialName("batch_number")
    val batchNumber: String,
    val quantity: Int,
    var winding: Int,
    var output: Int,
    var isolation: Int,
    var molding: Int,
    var crimping: Int,
    var quality: Int,
    var testing: Int
)
