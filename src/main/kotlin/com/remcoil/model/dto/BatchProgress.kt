package com.remcoil.model.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BatchProgress(
    val id: Long = 0,
    @SerialName("batch_number")
    val batchNumber: Int,
    @SerialName("is_accepted")
    val isAccepted: Boolean,
    @SerialName("operations_progress")
    val operationsProgress: Map<Long, Int>,
    @SerialName("control_progress")
    val controlProgress: Map<ControlType, Int>,
    @SerialName("locked_quantity")
    val lockedQuantity: Int,
    @SerialName("defected_quantity")
    val defectedQuantity: Int,
    @SerialName("accepted_quantity")
    val acceptedQuantity: Int
)
