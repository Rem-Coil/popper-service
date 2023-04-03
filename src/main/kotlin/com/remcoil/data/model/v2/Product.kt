package com.remcoil.data.model.v2

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Product(
    var id: Long = 0,
    @SerialName("product_number")
    var productNumber: String,
    var active: Boolean = true,
    @SerialName("batch_id")
    val batchId: Long
) {
    fun deactivated(): Product {
        return this.copy(
            id = id,
            productNumber = "$productNumber defective",
            active = false,
            batchId = batchId
        )
    }
}
