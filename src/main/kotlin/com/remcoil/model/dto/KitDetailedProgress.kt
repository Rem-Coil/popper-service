package com.remcoil.model.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KitDetailedProgress(
    val id: Long,
    @SerialName("kit_number")
    val kitNumber: String,
    @SerialName("acceptance_percentage")
    val acceptancePercentage: Int,
    @SerialName("batch_size")
    val batchSize: Int,
    @SerialName("operation_types")
    val operationTypes: List<OperationType>,
    @SerialName("batches_progress")
    val batchesProgress: List<BatchProgress>
) {
    constructor(kit: Kit, operationTypes: List<OperationType>, batchesProgress: List<BatchProgress>) : this(
        kit.id,
        kit.kitNumber,
        kit.acceptancePercentage,
        kit.batchSize,
        operationTypes,
        batchesProgress
    )
}