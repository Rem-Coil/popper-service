package com.remcoil.data.model.v2

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BatchProgress(
    val id: Long = 0,
    @SerialName("batch_number")
    val batchNumber: Int,
    @SerialName("operations_progress")
    val operationsProgress: Map<Long, Int>,
    @SerialName("control_progress")
    val controlProgress: Map<ControlType, Int>,
    @SerialName("locked_quantity")
    val lockedQuantity: Int,
    @SerialName("defected_quantity")
    val defectedQuantity: Int
)
