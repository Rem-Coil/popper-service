package com.remcoil.data.model.v2

import kotlinx.serialization.SerialName

data class BatchProgress(
    val id: Long = 0,
    @SerialName("batch_number")
    val batchNumber: Int,
    val operationsProgress: Map<Long, Int>,
    val controlProgress: Map<String, Int>,
    val lockedQuantity: Int,
    val defectedQuantity: Int
)
