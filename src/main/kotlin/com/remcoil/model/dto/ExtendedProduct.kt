package com.remcoil.model.dto

data class ExtendedProduct(
    val id: Long = 0,
    val productNumber: Int,
    val active: Boolean = true,
    val locked: Boolean,
    val batchId: Long,
    val kitId: Long
)
