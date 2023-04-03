package com.remcoil.data.model.v2

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: Long = 0,
    @SerialName("product_number")
    val productNumber: Int,
    val active: Boolean = true,
    @SerialName("batch_id")
    val batchId: Long
) {
    fun deactivated(): Product {
        return this.copy(active = false)
    }
}
