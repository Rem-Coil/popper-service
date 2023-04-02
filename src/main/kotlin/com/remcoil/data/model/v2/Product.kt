package com.remcoil.data.model.v2

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: Long,
    @SerialName("product_number")
    var productNumber: String,
    var active: Boolean,
    @SerialName("batch_id")
    val batchId: Long
)
