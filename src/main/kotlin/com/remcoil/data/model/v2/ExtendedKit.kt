package com.remcoil.data.model.v2

data class ExtendedKit(
    val id: Long,
    val kitNumber: String,
    val batchesQuantity: Int,
    val batchSize: Int,
    val specificationId: Long,
    val specificationTitle: String,
    val testedPercentage: Int
)
