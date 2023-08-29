package com.remcoil.model.dto

data class ExtendedKit(
    val id: Long,
    val kitNumber: String,
    val acceptancePercentage: Int,
    val batchesQuantity: Int,
    val batchSize: Int,
    val specificationId: Long,
    val specificationTitle: String,
    val testedPercentage: Int
)
