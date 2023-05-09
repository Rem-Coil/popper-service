package com.remcoil.data.model.v2

data class ExtendedProduct(
    val id: Long = 0,
    val productNumber: Int,
    val active: Boolean = true,
    val batchId: Long,
    val kitId: Long
)
