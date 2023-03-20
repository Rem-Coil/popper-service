package com.remcoil.data.model.kit

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Kit(
    val id: Long,
    @SerialName("kit_number")
    var kitNumber: String,
    @SerialName("batches_quantity")
    val batchesQuantity: Int,
    @SerialName("batch_size")
    val batchSize: Int,
    @SerialName("specification_id")
    val specificationId: Long
)
