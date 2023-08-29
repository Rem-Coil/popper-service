package com.remcoil.model.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Kit(
    val id: Long,
    @SerialName("kit_number")
    val kitNumber: String,
    @SerialName("acceptance_percentage")
    val acceptancePercentage: Int,
    @SerialName("batches_quantity")
    val batchesQuantity: Int,
    @SerialName("batch_size")
    val batchSize: Int,
    @SerialName("specification_id")
    val specificationId: Long
)
