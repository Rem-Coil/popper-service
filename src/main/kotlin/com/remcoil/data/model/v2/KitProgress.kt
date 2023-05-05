package com.remcoil.data.model.v2

data class KitProgress(
    val id: Long,
    val kitNumber: String,
    val batchSize: Int,
    val operationTypes: List<OperationType>,
    val batchesProgress: List<BatchProgress>
) {
    constructor(kit: Kit, operationTypes: List<OperationType>, batchesProgress: List<BatchProgress>) : this(
        kit.id,
        kit.kitNumber,
        kit.batchSize,
        operationTypes,
        batchesProgress
    )
}

